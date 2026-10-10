# VMMA-MJ

马偕贩卖机方案。

VMMA 是基于 Java Swing 和 Spring Boot 的药品柜供药桌面终端，通过 HTTP 接入中控系统，通过 TCP 接入药品柜控制器。

本文档以 `mj2` 分支当前实现为准，历史需求不代表当前界面已启用的功能。

## 当前功能

| 功能 | 当前范围 |
| --- | --- |
| 员工登录 / 登出 | 输入员工编号，经中控认证后直接进入调剂页 |
| 双条码调剂 | 输入药袋下方、上方条码，查询中控计划，驱动设备并回传储位级结果 |
| 补药提示 | 调剂页展示中控标记的告警药品 |
| 即时药存量 / 详情 | 按储位展开库存，查看药品图片及所选储位数量 |
| 系统参数 | 保存客户、终端、中控和连接参数；初始化失败时进入配置页，修改后须重启 |

**按调剂页「確認」或在上方条码框按 Enter 后，有有效计划就会自动开始出药。**「調劑操作確認」对话框中的「確定」主要用于结束后关闭，不是出药前批准按钮。

批次取药有内部实现，但入口隐藏；库存搜索栏隐藏；紧急/临时取药菜单未接入主窗口。当前没有病床/医嘱列表选择、独立 QRCode 流程、完整退药/补药/盘点流程，也没有已执行的闲置自动登出逻辑。详见 [架构中的功能边界](docs/ARCHITECTURE.md)。

## 文档导航

| 文档 | 内容 |
| --- | --- |
| [文档目录](docs/README.md) | 阅读顺序与维护约定 |
| [项目架构](docs/ARCHITECTURE.md) | 系统边界、模块、部署及关键流程 |
| [详细设计](docs/DETAILED_DESIGN.md) | 配置、数据模型、中控接口、设备协议与状态机 |
| [操作手册](docs/OPERATIONS.md) | 部署、登录、调剂、查询、维护与故障处理 |
| [变更记录](CHANGELOG.md) | 历史需求和修改记录 |

## 技术与项目结构

Java 8、Spring Boot 2.4.3、Swing、Spring Data JPA / SQLite、Apache MINA 2.1.4、RestTemplate / Apache HttpClient。单模块 Maven 项目，应用入口是 `com.mmh.vmma.VmmaApplication`。

```text
VMMA-MJ/
├── README.md
├── docs/                      # 架构、详细设计与操作手册
├── pom.xml                    # 依赖、打包和混淆配置
├── mvnw / mvnw.cmd            # Maven 3.6.3 Wrapper
├── src/main/java/com/mmh/vmma/
│   ├── controlcenter/         # 中控 HTTP 客户端与 DTO
│   ├── mina/                  # 药品柜 TCP 协议与取药流程
│   ├── jpa/                   # 本地配置持久化
│   ├── restful/client/        # HTTP 连接池与调度器
│   ├── systemvalue/           # 终端与端口配置模型
│   ├── ui/                    # Swing 界面及业务编排
│   └── utils/                 # JSON、图片、日期与字节工具
├── src/main/resources/        # YAML、Allatori 配置
├── src/test/java/com/hma/vmma/ # 上下文测试与手工实验程序
├── data/                      # 当前配置库和历史资料
├── images/                    # 运行时外部图片
├── lib/                       # 本地构建工具/历史库
└── VMMA.cmd                   # 历史 Windows 启动脚本
```

项目名称仍保留马偕针剂版本的历史标识：Maven 坐标为 `com.hma.vmma:MMH-VMMA:1.0`，主代码包为 `com.mmh.vmma`。应用是桌面程序，不会启动 Web 服务。

## 构建

准备 **JDK 8**。部分图片工具依赖 `sun.misc` 内部类，不能直接换用较新 JDK。首次使用 Wrapper 需要能下载 Maven 和项目依赖。

macOS / Linux（仓库中的 `mvnw` 未设置可执行位，直接通过 Bash 运行）：

```bash
java -version
bash mvnw -Dexec.skip=true package
```

Windows：

```bat
java -version
mvnw.cmd -Dexec.skip=true package
```

`-Dexec.skip=true` 跳过绑定到 `package` 的 Allatori 外部执行步骤，不会跳过 Spring Boot 和 Assembly 打包。主要产物为：

| 产物 | 用途 |
| --- | --- |
| `target/MMH-VMMA-1.0.jar` | Spring Boot 可执行 JAR，常规部署入口 |
| `target/MMH-VMMA-1.0-jar-with-dependencies.jar` | Assembly 依赖合并包，另一打包产物 |
| `target/obf-MMH-VMMA-1.0.jar` | 只有成功执行混淆步骤才生成 |

需要混淆发布时，先准备获授权的 `lib/allatori.jar`，核对 `src/main/resources/allatori.xml` 中的 JAR 名称和本地 Windows classpath，再运行不带跳过参数的 `package`。Allatori JAR 没有被 Git 跟踪，不能假设新克隆就拥有完整工具。

`pom.xml` 在 Surefire 配置中硬编码 `<skipTests>true</skipTests>`；要执行自动测试，需要先调整该配置，不能仅依赖 `-DskipTests=false` 覆盖。测试目录中的多数类是手工实验，不是自动化业务测试；上下文测试的包名也与主程序包不同，需要注意主配置定位及图形/网络依赖。不要将默认 `package` 成功视作端到端验证。

## 启动与配置

在有图形桌面的环境中，将可执行 JAR 与经批准的 `data/system.db`、`images/` 放入同一个运行目录，然后从该目录启动：

```bash
java -jar MMH-VMMA-1.0.jar
```

在项目根目录运行构建产物时使用：

```bash
java -jar target/MMH-VMMA-1.0.jar
```

默认配置见 `src/main/resources/application.yaml`：配置库为 `data/system.db`，日志为 `logs/vmma.log`，窗口标题为 `管制類藥品櫃管理系統`。中控地址、终端编号及药品柜连接参数存放在 SQLite 配置库中，由「系統參數設置」管理，不是 YAML 中的 `server.port`。

可按 Spring Boot 规则提供外部 YAML，例如：

```bash
java -jar MMH-VMMA-1.0.jar --spring.config.additional-location=file:./application.yaml
```

外部 YAML 是部署方另行提供的文件。`data/create_tables.sql` 是历史业务表脚本，**不适合初始化当前配置库**。`VMMA.cmd` 包含固定磁盘路径、拨号逻辑及旧版 JAR 文件名，不能作为当前版本的通用启动命令直接使用。

## 运行注意事项

设备动作与中控结果回传不在同一个事务中；网络故障后可能出现已经出药但结果未确认的状态。必须核对设备、实物和中控记录，不能简单重试调剂。

本地配置库、日志及历史数据可能包含现场配置或业务资料。不要将真实账号、令牌、患者数据或完整生产日志写入文档、公开提交或工单。部署和备份步骤见 [操作手册](docs/OPERATIONS.md)。