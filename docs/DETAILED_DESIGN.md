# 详细设计

> 基线：`mj2` / `bbf189c`，2026-10-10。[架构](ARCHITECTURE.md) · [操作手册](OPERATIONS.md)

本文描述客户端实际实现。DTO 字段不是服务端完整契约；设备字段不是厂商完整规格。联调时必须同时核对中控版本和设备协议。

## 1. 配置与本地存储

### 1.1 Spring 配置

默认资源为 `src/main/resources/application.yaml`。

| 配置项 | 默认值 / 作用 |
| --- | --- |
| `server.type` | `out`；主窗口有注入但未实际使用，不能据此推断已有完整退药流程 |
| `server.title` | `管制類藥品櫃管理系統`；窗口标题 |
| `spring.datasource.url` | `jdbc:sqlite:data/system.db`，相对进程工作目录 |
| `spring.datasource.type` | `org.sqlite.SQLiteDataSource` |
| `spring.datasource.driver-class-name` | `org.sqlite.JDBC` |
| `spring.jpa.database-platform` | `com.enigmabridge.hibernate.dialect.SQLiteDialect` |
| `spring.jpa.show-sql` | `false` |
| `logging.level.root` | `INFO` |
| `logging.level.com.mmh.vmma` | `TRACE` |
| `logging.file.name` | `logs/vmma.log` |
| `logging.logback.rollingpolicy.max-file-size` | `5MB` |
| `logging.logback.rollingpolicy.max-history` | `90`；滚动历史配置，不是业务审计保留承诺 |

`application-return.yaml` 只有日志和数据库配置，日志分类还是 `com.hma.vmma`。加载此 profile 不会凭空增加退药页面；它也没有覆盖默认 `server.type`。

### 1.2 配置表

`SystemConfig` 是唯一 JPA 实体，`SystemConfigRepository` 继承 `JpaRepository<SystemConfig, String>`。实体声明 `t_SystemConfig`，当前数据库实际表名为 `t_system_config`，由命名策略映射。

当前 `data/system.db` 的表结构如下；原始 `vachar` 拼写照录，不应把这段当成整库迁移脚本：

```sql
CREATE TABLE t_system_config (
    key vachar(100) PRIMARY KEY NOT NULL,
    value BLOB NOT NULL
);
```

`value` 保存 `FastJsonUtils.serialize` 产生的 JSON 字节，不是加密文件。序列化与反序列化使用平台默认字符集，部署环境应保持一致。

`data/create_tables.sql`、其他历史数据库、CSV 和清理脚本属于旧版资料；当前源码没有用这些表实现本地处方/库存管理。不能用该 SQL 初始化当前配置库，也不能把 `delete_result.cmd` 当作本版维护步骤。

### 1.3 JSON 配置模型

| 配置 key | 模型 | 字段 |
| --- | --- | --- |
| `cabinet` | `VMConfiguration` | `customer`、`terminalid`、`portModes[]` |
| `control_center` | `CenterControl` | `address`，即中控基础 URL |

`PortMode.mode`：`0` 串口、`1` TCP Client、`2` TCP Server。TCP 字段为 `address/portNumber`；串口字段为 `name/baudRate/dataBites/parity/startBits/stopBits/flowControl`。`dataBites` 等拼写照源码保留。

下面只是结构示意，不是可直接导入的现场配置：

```json
{
  "customer": "<终端显示客户名称>",
  "terminalid": "<中控登记的终端编号>",
  "portModes": [
    {"mode": 1, "address": "<药柜 IPv4>", "portNumber": "<TCP 端口>"}
  ]
}
```

```json
{"address": "https://<中控主机>/<基础路径>"}
```

配置页保存客户名称和终端编号，不保存 `server.type/server.title`。当前启动遍历端口时复用同一个 TCP 客户端，没有按端口创建独立 Spring 上下文或队列；端口数组不能作为多柜隔离支持的依据。

### 1.4 配置界面与校验

| 界面项 | 默认 / 行为 |
| --- | --- |
| `*用戶名稱`、`*終端編號`、`*中央控管URL` | 无配置时空；星号为必填项 |
| `端口類型` | 默认 `TCP/UDP`，另有 `RS232C(串口)` |
| `新增端口` | 默认关闭，打开后保存才追加端口 |
| `連接方式` | 默认 `TCP/UDP Client`，另有 Server |
| `服務IP`、`連接端口` | 重置为 `127.0.0.1` 和 `20000`；不是现场推荐地址 |
| 串口设置 | COM1、9600、数据位 5、None 校验、停止位 1、None 流控 |
| `保存/刪除/退出` | 持久化、删除选中端口、按初始化状态退出 |

标签包含 UDP，但实际没有 UDP 实现；串口和 TCP Server 也只有初始化日志。正式配置只能使用已确认的 TCP Client 路径。

客户/终端字段以 trim 后非空判断，保存仍使用原文本。新增 TCP Client 要求 IP/端口非空，但 IP 格式报错分支未可靠阻止保存；更新已有端口的网络校验也不完整。端口键入限制为数字，没有完整 `1–65535` 范围校验。

IP 键入过滤存在对首位零、零段和光标位置的限制，不能视为通用 IPv4 编辑器。URL 校验主要要求 URI 可解析且有 host，没有限制 scheme 或检测服务可达性。

### 1.5 保存顺序与生效

「保存」先更新并 `saveAndFlush(cabinet)`，再验证和保存 `control_center`。两份配置不是界面级原子事务，后者验证失败时前者可能已保存；没有明确的整体成功弹窗。

端口表可排序，但更新/删除直接用选中视图行索引原始数组，没有行索引转换；维护时不能在排序后盲目更新/删除。没有保证至少一条连接或禁止重复连接的规则。

保存不重建设备连接，也不热更新 `GlobalData`。初始化失败路径的「退出」提示重启后结束应用；成功初始化路径却调用未注册的功能页，不能当作可靠返回菜单或热重载。正常登录后也没有可达的配置菜单。

## 2. 中控接口

### 2.1 HTTP 客户端

所有业务接口均为客户端发出的 HTTP POST，URL 由配置的中控基础地址和固定路径拼接；基础地址末尾是否带 `/` 均有处理。

`RestTemplateConfig` 使用 Apache HttpClient。连接建立与连接池申请超时各 30 秒，读取超时 60 秒，连接池总上限 50。服务器未给出 keep-alive 时使用 20 秒；后台每 10 秒清理过期连接和空闲超过 30 秒的连接。调度线程池大小为 50。

HTTPS 配置使用 `TrustSelfSignedStrategy`；本地网络配置不等于端到端安全保障。接口鉴权字段在 JSON 请求体中，不能假设统一使用 `Authorization` 请求头。

多数请求使用 `clientId`、`datetime`、`token`、`data` 外层结构；登录没有 `token`，行为接口使用 `terminalId`。时间默认格式为 `yyyy-MM-dd'T'HH:mm:ss.SSSZ`。

### 2.2 接口清单与鉴权来源

| 客户端类 | 路径 | 关键请求与返回 |
| --- | --- | --- |
| `RestfulDeviceInit` | `/system/init` | `data.terminalId/termType/ipAddress/macAddress`；返回 `id/token/expireddate` |
| `RestfulLogin` | `/user/login` | `data.loginType/userName/encPassword`；返回用户 token、身份、roles、menuItems |
| `RestfulLogout` | `/user/logout` | 用户 `token`，客户端编号和时间；返回结果与消息 |
| `RestfulProvideQuery` | `/user/provide/query` | 条码、病历/医嘱与可选药品请求；返回待调剂药品及各储位分配 |
| `RestfulProvideResult` | `/user/provide/result` | 条码、病历/医嘱和储位级实际数量/状态；返回药品/储位库存信息 |
| `RestfulTMMedicines` | `/system/tmedicines` | `data.terminalId/medicineId/photo`；返回药品及储位库存 |
| `RestfulActions` | `/user/action` | `data[]` 内的 `ActionId/ActionResult`；返回操作记录结果 |

初始化客户端使用源码固定的初始化鉴权标记；初始化返回 token 后存入 `GlobalData.systemToken`。库存客户端仅在调用方没有设置 token 时补系统 token。登录不依赖该字段；登出、调剂和行为客户端不会统一自动补用户 token，由调用方传入。

通用 `RestfulClient` 返回 `postForEntity`，具体业务客户端使用 `postForObject`。查询、结果与库存接口先收字符串再经 Fastjson 解析；其他客户端直接接收类型对象。

旧 README 提到的 `provide/basic`、`medicine` 和 `IsProvided` 不是当前这些客户端的路径或 DTO 字段。接口契约应以这里的源码与服务端联调结果为准。

### 2.3 登录与菜单模型

`ReqLogin.data` 包含 `loginType`、`userName` 和 `encPassword`。`CODES` 定义账号模式 `id` 和卡号模式 `card`，也保留人脸/指纹标记；常量存在不代表对应按钮已实现。

`ResLogin.data` 的身份字段为 `userId/userName/department/userInfo/introduction/avatar/email`，另有 `token`、字符串数组 `roles` 和数组 `menuItems`。

每个菜单对象含 `appid/icon/title/active/subItems`，每个子菜单含 `appid/icon/title/active/url`。客户端页面和按钮映射仍在本地代码中，并非收到任意 URL 就生成完整页面。

`AESUtils.getSha256` 提供对输入文本与源码固定共享值的 UTF-8 字节计算 SHA-256、再 Base64 编码的工具。但当前员工编号登录把 `encPassword` 设为空，没有调用该摘要路径；不能把工具存在写成当前 UI 实现了密码认证。固定共享值不在文档中列出。

### 2.4 查询与结果模型

`ReqProvideQuery` 外层为 `clientId/datetime/token/data`。`data` 含 `terminalId/chartNo/PhrOrderNo/barcodeNo1/barcodeNo2/medicinedata`；可选请求药品项为 `boxId/medicineId/takeQty`。

`ResProvideQuery.data` 含终端、病历、医嘱、两段条码和药品数组。每个药品项包括：

| 字段组 | 字段及意义 |
| --- | --- |
| 药品与处方 | `medicineId/medicineName/qty/doseUnit/fq/control` |
| 出药请求 | `takeQty/takeUnit` |
| 提示与展示 | `photo/patientName/medNo/scrn/confirmType` |
| 批号与效期 | `lot/expiredDate` |
| 储位分配 | `boxInfo[]`：`boxId/stockQty/takeQty` |

`ReqProvideResult.data` 保留条码、病历/医嘱和终端字段；`medicinedata[]` 按储位记录 `boxId/medicineId/takeQty/status`。状态常量 `CONSTANTS` 为 `0` 成功、`1` 取药异常、`2` 数量不足、`3` 药品信息错误；实际分支如何构造状态见后文业务流程。

`ResProvideResult.data.medicinedata[]` 含 `medicineId/stockQty/isAlarm/boxInfo`。其嵌套储位 DTO 的 `takeQty` 当前是 **boolean**，不是请求中的 double；不能照搬该响应类型作为数量模型。

模型同时保留 `ChartNo/chartNo`、`PhrOrderNo/phrOrderNo`、`BoxId/boxId` 等大小写差异，并混用 Jackson 与 Fastjson。上述字段名照源码描述，不能在未确认序列化结果和服务端契约时直接统一大小写。

### 2.5 库存模型

`ReqTMedicines.data.photo` 控制是否请求图片；`medicineId` 可用于单药品查询。`ResTMedicines` 带 `reccount` 和 `data[]`。

每个返回项含药品 ID、名称、学名、告警阈值 `alarmQty`、告警标记 `isAlarm`、总量 `quantity`、最大总量 `maxQuantity`、单位/途径/厂商/包装/描述/管制标记/批号/效期/图片及其他医院扩展字段。`boxInfo[]` 含 `boxId/maxQuantity/quantity`。

UI 展示时可将一个药品的多个储位展开为多个 `VMMedicine` 行，分别保留药品级总量和储位级数量。这里显示的是中控返回库存，不是客户端直接测量的实时机械库存。

### 2.6 结果码与异常边界

`ResEntity.result` 的成功常量是 `20000`，不是 HTTP 状态 `200`。常见定义如下：

| 结果码 | 源码常量意义 |
| --- | --- |
| `40000` | 缺少参数 |
| `40100/40101` | 未授权 / 无账号 |
| `40102` | 密码与终端编号错误常量共用此值，须看返回消息 |
| `40400/42200` | 接口不存在 / 接口错误 |
| `50008/50000` | token 错误 / 系统错误 |
| `20001/20002/20003/20004` | 唯一性、表、写入和查询/SQL 错误 |

HTTP 失败通常由 RestTemplate 抛出，UI 再处理。没有跨接口自动 token 刷新、统一离线降级或持久化重试队列；收到异常不能假设服务端一定没有处理原请求。

## 3. 界面与业务详细流程

### 3.1 初始化与会话

`VmmaApplication` 显示模态 `InitSystem` 后再显示 `MainWindow`。初始化从 `cabinet/control_center` 读取配置，把客户、终端和基础 URL 写到 `GlobalData`，调用 TCP 连接，再注册中控。

注册请求发送终端编号、`termType="1"`（药柜终端）、MAC 地址，IP 字段为空。初始化响应的 token 与完整对象存入全局状态，但没有完整业务成功码校验。TCP 连接等待按每 500ms 计数，超过 120 次设失败码；该返回只退出端口 lambda，后续初始化仍可能继续。

员工编号登录发出 `loginType="id"`、输入编号和空密码字段。中控成功后整个 `ResLogin` 存入 `MainWindow.loginUser`，直接切调剂页；失败则显示服务端消息或记录请求异常。无角色选择、密码框或修改密码流程。

登录页再次显示会清本地身份。调剂页「退出」请求 `/user/logout` 后切登录，即使中控异常也会清本地会话；这不是结束进程。登录页右上角无文字按钮调用 `exitSystem()`，鼠标进入才显示退出图标。

源码内置口令分支不会建立有效 `ResLogin`，后续用户访问可能报错，不能当作维护入口。管理/退出权限检查存在注释代码，但当前核心页面没有完整本地权限保护。`SIGNOUT_TIMEOUT=45` 和计数字段存在，却没有定时计数/达到阈值登出的执行逻辑。

### 3.2 双条码计划查询

调剂页下方条码按 Enter 转上方框；上方 Enter 或「確認」调用 `provideDrug()`，发 `/user/provide/query`。按钮路径要求两个输入非空，上方 Enter 路径只检查上方框，且没有条码格式/长度/校验位检查。

请求使用当前用户 token、终端编号和两段原始条码，病历号/医嘱号为空，没有用户选择的药品数组。中控返回计划后，UI 清空输入并显示 `DlgGeneralProviding`；失败显示原因/读取错误，空药品数组显示「沒有待取藥物列表」。

查询等待动画由普通线程更新标签，HTTP 调用仍可阻塞事件线程。没有“病床→医嘱→药品”选择步骤或独立 QRCode 解析。

### 3.3 自动出药编排

「調劑操作確認」窗口显示后自动调用 `providingMedicine()`，先禁用「確定」，无独立取消按钮，不能通过普通窗口关闭取消动作。

遍历 `medicinedata[]` 再遍历 `boxInfo[]`，以 `medicineId + "-" + boxId` 建结果项，初始状态 `-1`、已取量 `0`。为每个储位提交：

```text
Vendout(id=medicineId-boxId,
        column=Integer.valueOf(boxId),
        count=ceil(box.takeQty),
        callback=当前对话框)
```

`planTakeTimes` 是储位任务数，不是总包数。储位必须是可转换的整数，数量向上取整；并无全计划预校验、零/负数过滤或失败取消剩余任务。

对话框显示药品、单位、双条码、储位、计划量/库存、批号/效期、病历/患者/领药号/处方日期/频次。效期和处方日期经工具尝试转本地时区，转换失败保留原文本。图片更新始终取计划首个药品图片，多药品场景不能只据图片判断当前药品。

`takeQty > stockQty` 有提示，但仅从显示函数返回，不阻止任务入队；它不是可靠的库存安全阻断。

### 3.4 储位结果归集与回传

| 回调 | UI 处理 |
| --- | --- |
| `600` 开始 | 按流程 key 更新药品/储位展示 |
| `601` 进度 | 显示当前尝试包数，不是成功计数 |
| `0` 成功 | 储位状态 `0`、数量 `loop` |
| `501/503` 等终结失败 | 储位状态 `1`、数量 `loop - 1`，显示设备消息 |

所有储位终结后启用「確定」并 POST `/user/provide/result`，包含返回计划中的病历/医嘱/条码和每个药品/储位的数量、状态。失败项也回传，不是旧 README 所称“仅发送成功药品”。

`loop-1` 只表示之前被软件判定完成的包数，失败当前包是否物理落药仍须核对。最后一项提示可能覆盖先前错误，界面没有完整整单成功/部分成功总结。

回写业务失败显示黄色服务端原因，异常提示联系管理员，无本地持久化待办、自动重试或人工重试按钮。「確定」在回写调用前已启用，不能据按钮状态判断服务端已落账。

空 `boxInfo`、非数字储位或回调异常可能使任务计数无法闭合，窗口一直不能正常关闭。页面结果 Map 和任务计数也没有统一同步保护。

### 3.5 告警、库存与详情

进入调剂页查询 `/system/tmedicines`，`photo=false`，仅显示中控 `isAlarm=true` 的药品。可见列为药物编号、药名、警示量、现存总量；最大储量在模型中但隐藏。

进入库存页查询同接口，`photo=true`，将药品按储位展开。可见列为储位号、药名、现存总药量、储位现存量、最大量和药物编号；同药品的总量在每个储位重复。初始顺序为告警优先，再按数值储位号。

选行后「詳情」新建只读 `DlgMKDrugStatus`，展示药品/包装/单位/管制类型/储位数量/最大量/图片等。学名控件存在，但库存转换没有填充该字段；「藥品劑型」实际取 `doseRoute`。详情没有补药、改库存或开门操作。

库存和告警主要在页面显示时查询，翻页/详情返回使用内存列表，没有持续轮询。「即時」不能解读成直接传感器测量。搜索栏高度为零，不能按可见功能写操作步骤；页码输入也没有完整范围保护。

### 3.6 保留但不可常规使用的流程

`DlgBatchProvide` 已有药品数量输入、请求计划、逐储位出药及结果回传。查询用固定批次条码标记和秒级时间，病历/医嘱为空，由中控分配储位。

唯一库存入口「批次取藥」明确隐藏。其小数输入/整数解析、库存阻断、空计划和回写失败处理也不完整；目前不能通过公开按钮执行，更不能仅打开入口就视为生产可用。

`JPFunctions` 的一般/紧急/临时/管理菜单未注册进主窗口。`JPCabinet` 为简单布局占位；图片模板及人脸相关历史库也不是完整业务流程。

## 4. TCP 连接、队列与协议

### 4.1 连接拓扑与保活

`SignageIFClient` 使用 MINA `NioSocketConnector`，`InitSystem` 把配置的地址/端口传入 `connect()`。客户端、`RequestQueue`、`ResponseQueue` 是共享 Spring 单例，没有每柜子上下文或按端口隔离。

多条 TCP Client 配置会对同一连接器反复修改默认远端、启动连接线程；发送又要求受管会话数恰为 1。因此不能把配置数组写成多个柜体独立连接支持。

| 行为 | 实际设置 |
| --- | --- |
| 连接超时 | 30,000ms |
| 首次连接 | 普通线程，无限尝试；退避 1、2、4、8、16、32 秒，随后保持 32 秒 |
| 断线重连 | 过滤器的 `sessionClosed` 中同步等待重连，没有明确停止标志 |
| 空闲参数 | `setIdleTime(BOTH_IDLE, 30000)` 使用秒，实际为 30,000 秒，不是 30 秒 |
| 收发缓冲 | 1024 字节配置，不是协议最大帧长 |
| 保活 | 会话打开立即发，之后每 15 秒发固定原始报文 |

固定保活报文为：

```text
02 07 00 01 01 41 11 01 00 00
```

它使用 ObjectID/IndexID `01`、DataID `16657/0x4111`、GET、无块，不是 `ReqMessage_16647`。客户端没有专用保活回复分派、丢失计数或超时关闭策略。保活线程保留首次 session，重连和中断退出处理不完整，不能承诺重连后自动恢复保活。

连接状态 CONNECTED 主要反映连接器活跃，不代表设备状态允许出药。主动关闭、销毁和重连没有完整取消机制，不应当成可任意停止再启动的服务 API。

### 4.2 队列

`RequestQueue` 的唯一工作线程 FIFO 取出 `FlowFactory`，同步执行 `start()`。同队列储位流程串行，失败返回值不阻止下一任务。无任务时约每 50ms 清空响应队列，异步事件不会作为持续状态或历史保存。

`ResponseQueue` 使用 `LinkedList<Message>` 和部分加锁操作，不是阻塞持久化队列，没有容量限制、接收时间或会话标记。原始列表可被直接访问；销毁清队列不能中止正在执行的 `Vendout`。

### 4.3 帧布局

以下偏移基于**解转义后的帧**：

| 偏移 | 长度 | 字段 / 编码 |
| --- | --- | --- |
| `0` | 1 | STX=`0x02` |
| `1–2` | 2 | length，低字节在前；offset 3 起的 header + blocks 长度 |
| `3` | 1 | ObjectID，普通消息构造为 `0x26`；固定保活为 `0x01` |
| `4` | 1 | IndexID，请求/响应关联序号 |
| `5–6` | 2 | DataID，高字节在前 |
| `7` | 1 | 消息类型 |
| `8` | 1 | ItemNo |
| `9` | 1 | BlockCount |
| `10…` | 可变 | 数据块 |

无正文头长 7、总帧长 10，即 `total = length + 3`。消息类型为 EVENT=`00`、GET=`01`、SET=`02`、SEND=`04`、CLEAR=`08`、PUSH=`42`、NONE=`FF`。

每块为 `type(1) + count(1) + data`：

| type | 类型 | 每项分配宽度 |
| --- | --- | --- |
| `01` | BYTE | 1 |
| `02` | SHORT | 2，高字节在前 |
| `04` | INT | 4，高字节在前 |
| `08` | STRING | 逐字节输出，没有字符集协商 |
| `10` | BCD | 4，当前按普通 INT 输出，没有 BCD 编码转换 |

编码器保留首个 STX，其余字节转义 `02 → 7D 22`、`7D → 7D 5D`。解码器累积输入并按长度确认帧。源码没有 ETX、CRC、协议版本或认证字段，不能补写成厂商规格。

IndexID 是静态共享计数，并非每柜独立；实际值循环 1…128，其中 128 的 byte 是 `0x80`。创建和发送过程都会推进计数，没有统一同步。

### 4.4 消息清单

| DataID（十进制 / 十六进制） | 当前实现与用途 |
| --- | --- |
| `10246 / 2806` | PUSH：BYTE 货道号；响应是命令确认，实际出货还需事件 |
| `16650 / 410A` | GET/SET 类：查询或设置销售状态；当前出药只用 GET，返回状态 byte |
| `25089 / 6201` | PUSH 无块，退币准备指示；与 24939 事件配合 |
| `25090 / 6202` | PUSH 四块：状态、货道列表、余额、16 字节卡字段；当前固定状态 `03`、单货道、余额 0、16 个字符 `'0'` |
| `10343 / 2867` | 出货结果事件，当前检验结果 bit0 |
| `10363 / 287B` | 贩卖报告事件，当前流程主要确认到达 |
| `16741 / 4165` | 状态事件，准备检验 bit1，出货恢复检验 bit0 |
| `24934 / 6166` | 结算事件，当前流程确认到达，不校验完整结算业务值 |
| `24939 / 616B` | 退币事件，当前准备阶段确认到达 |
| `16647 / 4107` | 保留“最大货道数”GET 类，未实际调用，长度构造也不一致 |
| `16657 / 4111` | 固定保活 ID，无专用消息类或接收分派 |

接收只分派前九个 ID，不按 type 独立区分；其余 ID 丢弃。`ResMessage_00000` 只是通用包装类，不表示 ID 0 已被支持。

`EvtMessage_24934` 按固定偏移读取十块，至少需要 71 个解转义字节：

| 块 | 数据偏移 | 解析字段 |
| --- | --- | --- |
| 0 | `12–13` | 两个 byte，未命名 |
| 1 | `16–19` | 两个 BE short：`cashAmount/cardAmount` |
| 2 | `22–25` | BE int：`id` |
| 3 / 4 / 5 | `28 / 31 / 34` | 各一个未命名 byte |
| 6 | `37–40` | BE int：`cardBlance` |
| 7 | `43–46` | BE int：`cardBfBlance` |
| 8 | `49–52` | BE int：`cardAfBlance` |
| 9 | `55–70` | 16 个未命名 byte |

字段拼写照源码保留；不根据 type/count 动态解释厂商扩展。

## 5. Vendout 状态机与回调

`Vendout` 继承 `FlowFactory`，构造 id、货道 column、次数 count 与回调。id 只用于本地识别，没有发给设备。每包都完整执行四个动作：

| 阶段 | 请求 | 完成条件 / 等待阈值 |
| --- | --- | --- |
| 状态查询 | GET 16650 | DataID + IndexID 匹配；`saleState & 03` 非零；约 10 秒无回应报错 |
| 退币准备 | PUSH 25089 | 同 IndexID 确认 + 24939 事件；约 10 秒无所需进展报错 |
| 销售设置 | PUSH 25090 | 同 IndexID 确认 + 16741 bit1 就绪；约 10 秒无所需进展报错 |
| 开始出货 | 先等待 1000ms，再 PUSH 10246 | 同 IndexID 确认、10363 报告、24934 结算、10343 bit0 成功、16741 bit0 恢复全部到齐 |

出货阶段使用 30 秒等待阈值，部分新进展可重置计时，不是整包或整单硬性 30 秒期限。退币/报告/结算等事件不与本次请求的 IndexID 或货道完整关联，仅靠 ID/状态位累计条件。

全流程开始回调 `600(loop=0, message=id)`；每次尝试前回调 `601(loop=i+1)`；全部 count 包完成才回调 `0(loop=count)`。该成功只覆盖当前储位任务，不代表整单或中控回写成功。

失败时回调 `501/503` 等，loop 为失败当前包序号 `i+1`，立即结束当前任务余下包数。当前 Vendout 不产生 `502/504`，这些只是公共常量，不能当成设备厂商故障字典。

虽然源码声明重试次数 2，但当前 RETRY 路径会在首次重试分支报「無法識別返回碼」退出，没有实际自动重取第二次。流程状态字段也未在所有分支正确转最终状态，应以回调结果为依据，不能只读 `getStatus()`。

## 6. 并发、错误处理与已知限制

| 范围 | 影响及设计边界 |
| --- | --- |
| Swing 线程 | 初始化/动画/设备回调跨线程直接改控件；多处 HTTP/数据库操作来自事件线程，可能卡顿 |
| 共享状态 | 页面、用户、计划计数和结果 Map 缺少统一同步；任务边登记边入队 |
| 身份/权限 | 当前员工编号模式无密码输入，本地管理检查不完整；不可依赖内置口令或自动登出 |
| 设备隔离 | 单客户端/队列复用，无多柜事务归属或每设备连接隔离 |
| 响应关联 | ACK 只比 DataID/IndexID，事件只比 ID/位；迟到/重复事件可能混入当前流程 |
| 帧健壮性 | unsigned 处理、尾部转义分片、坏帧重同步及块长度校验不完整 |
| 未使用协议能力 | SHORT 输出存在 fall-through；16650 SET、可变多货道长度等不能当作已验证能力 |
| 停止与销毁 | 无统一 cancel/abort；重连/保活/请求线程不能保证可靠中止 |
| 结果一致性 | 无物理动作与 HTTP 事务、持久化回写重试或自动补偿 |
| 输入与展示 | 库存提示不阻断出药，页码和配置索引处理不完整，图片可能仍为首项 |

`SocketServer` 是 test 目录中的手工 TCP 服务，监听 20000，每隔 15 秒依次发送固定 16741、24934、10363 示例。它没有按请求生成 ACK、状态查询回复或 10343 出货结果，不是完整出药模拟器，也不是自动测试。

其他测试目录类主要用于布局、图片、键盘和集合实验。`VmmaApplicationTests` 只有空的上下文加载测试，且处于不同顶层包；POM 默认跳过测试。设备动作、异常对账和现场操作必须另外经过批准的联调。

## 7. 主要源码依据

| 主题 | 源码 |
| --- | --- |
| 启动 / 初始化 | [VmmaApplication](../src/main/java/com/mmh/vmma/VmmaApplication.java)、[InitSystem](../src/main/java/com/mmh/vmma/ui/dialogs/InitSystem.java) |
| 页面 / 会话 | [MainWindow](../src/main/java/com/mmh/vmma/ui/frames/MainWindow.java)、[JPUserLogin](../src/main/java/com/mmh/vmma/ui/panels/JPUserLogin.java) |
| 配置 | [JPVMConfig](../src/main/java/com/mmh/vmma/ui/panels/JPVMConfig.java)、[SystemConfig](../src/main/java/com/mmh/vmma/jpa/entities/SystemConfig.java) |
| 调剂 / 结果 | [JPMKGeneralProvide](../src/main/java/com/mmh/vmma/ui/panels/JPMKGeneralProvide.java)、[DlgGeneralProviding](../src/main/java/com/mmh/vmma/ui/dialogs/DlgGeneralProviding.java) |
| 库存 / 保留入口 | [JPMKDrugsStatus](../src/main/java/com/mmh/vmma/ui/panels/JPMKDrugsStatus.java)、[DlgBatchProvide](../src/main/java/com/mmh/vmma/ui/dialogs/DlgBatchProvide.java) |
| HTTP 参数 | [HttpClientConfig](../src/main/java/com/mmh/vmma/restful/client/HttpClientConfig.java) |
| TCP / 编解码 | [SignageIFClient](../src/main/java/com/mmh/vmma/mina/socket/signage/SignageIFClient.java)、[SignageIFDecoder](../src/main/java/com/mmh/vmma/mina/socket/signage/SignageIFDecoder.java)、[SignageIFEncoder](../src/main/java/com/mmh/vmma/mina/socket/signage/SignageIFEncoder.java) |
| 出药 / 队列 | [Vendout](../src/main/java/com/mmh/vmma/mina/socket/signage/flow/Vendout.java)、[RequestQueue](../src/main/java/com/mmh/vmma/mina/socket/signage/queue/RequestQueue.java) |
| 消息布局 | [Header](../src/main/java/com/mmh/vmma/mina/socket/message/signage/Header.java)、[Block](../src/main/java/com/mmh/vmma/mina/socket/message/signage/Block.java) |
| 打包 / 默认参数 | [pom.xml](../pom.xml)、[application.yaml](../src/main/resources/application.yaml) |
