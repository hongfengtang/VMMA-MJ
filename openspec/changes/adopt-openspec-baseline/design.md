## Context

- OpenSpec 1.3.1 已初始化（schema：spec-driven），生成了 GitHub Copilot、Claude、Cursor 三套 `/opsx` 文件。`openspec/config.yaml` 已写入项目上下文和产物规则，但所有文件都还没有提交。
- 现有 `docs/` 约 155KB，基线为 `mj2` / `b8e9ebe`，详细描述了架构、协议、状态机和操作，但没有“规格”这一层。需求只记录在 `CHANGELOG.md` 和 commit message 中。
- 仓库按客户分支开发：`mj2`（马偕）、`ghnz`（高雄荣总测试版）、`master`（基本停滞）。它们自 `da2588b` 分叉后独立演进；`SignageIFDecoder`、`Settings` 等通用修复在两个客户分支上各做了一次，结果文件完全相同。
- 没有自动化测试（Surefire 硬编码 `skipTests=true`），基线规格只能通过对照源码核实，必要时联调或现场确认。

## Goals / Non-Goals

**Goals:**
- 提交 OpenSpec 脚手架，让团队可以直接使用 `/opsx` 工作流。
- 为 `barcode-dispensing`、`vendout-flow`、`cabinet-protocol` 建立主规格，使后续变更可以使用 `MODIFIED`。
- 把 OpenSpec 接入现有的文档维护规则和 CHANGELOG 约定。
- 记录基线梳理中发现的、与预期不符的行为，供后续立项。

**Non-Goals:**
- 不修改任何 Java 代码、POM 或运行配置，也不修复下文的发现项。
- 不为其余能力建立规格。以下名称已预留，等到首次变更时再建立：`staff-session`、`system-initialization`、`system-configuration`、`inventory-view`、`control-center-api`。
- 不处理 `ghnz`、`master` 分支。

## Decisions

### D1：通过变更建立基线，而不是直接写 `openspec/specs/`
用 proposal 固定基线提交和范围，用 tasks 列出逐条核对清单，归档后保留可追溯的记录。直接写主规格更快，但没有评审和核对的痕迹。注意：归档时自动生成的主规格中 Purpose 为 “TBD”，需要在归档后补写。

### D2：能力边界与源码模块、文档章节一一对应
| 能力 | 源码 | 文档 |
| --- | --- | --- |
| `barcode-dispensing` | `ui/panels/JPMKGeneralProvide`、`ui/dialogs/DlgGeneralProviding`、`controlcenter/RestfulProvide*` | DETAILED_DESIGN 第 3.2–3.4 节 |
| `vendout-flow` | `mina/socket/signage/flow/*`、`queue/*` | DETAILED_DESIGN 第 5 章、第 4.2 节 |
| `cabinet-protocol` | `mina/socket/signage/*`、`mina/socket/message/*` | DETAILED_DESIGN 第 4 章 |

这样对应后，`docs/README.md` 第 6 节的“代码变动 → 文档同步”对照可以直接映射到规格。备选方案是把调剂和出药合成一个能力，但界面编排与设备状态机的变更来源不同（客户界面需求 vs 设备定制），分开后差异更清楚。

### D3：基线只写已核实为真的行为
每条 Requirement 都对照 `b8e9ebe` 的源码核实过，场景只写在核实范围内成立的情形。疑似缺陷不写成 SHALL，而是记入下文“发现”，由后续变更用 `ADDED` 或 `MODIFIED` 处理，修复前后的差异因此可以评审。
- 备选 A：按“预期行为”写。这样规格会对当前系统做出不成立的陈述。
- 备选 B：把缺陷行为也写成 SHALL。这等于认可缺陷，后续修复也要去 `MODIFIED` 一条描述缺陷的需求。

### D4：规格写 WHAT，文档写 HOW
规格只写在界面、中控 HTTP 或设备 TCP 边界上可观察的行为；类名、字段表和字节布局引用 DETAILED_DESIGN 的章节，不复制。`docs/` 仍是权威设计和运维文档。`docs/README.md` 第 6 节新增一条约定：需求或行为变更先建 OpenSpec 变更，归档后同步主规格和对应文档。

### D5：客户定制用【马偕定制】前缀标注
前缀写在 Requirement 名称中，`openspec show` 可以直接看到，也便于 grep，归档后依然保留，移植时容易识别。备选是正文中的元数据行（如 `**定制**: 马偕`），可见性较低，而且放在第一行之前会影响校验器取到的 SHALL 行。

### D6：规格只放在 `mj2`，只描述 `mj2`
`master` 停滞且没有合并流程，不适合承载共同规格。`ghnz` 与 `mj2` 的调剂页、登录页差异很大，如果引入 OpenSpec，需要单独建立基线。`config.yaml` 规则要求通用修复在 Impact 中注明是否需要同步到其他分支。

### D7：提交三套工具的生成文件
`.github/`（Copilot）、`.claude/`、`.cursor/` 由 `openspec init` 生成，可用 `openspec update` 重新生成，内容很小。默认全部提交，团队用不到的工具目录以后可以删除（见 Open Questions）。

## 出药结果语义与对账（基线所描述的现状）

本变更不改变出药行为，以下只是把规格中涉及的语义汇总，对账步骤见 OPERATIONS.md 第 6.3 节：
- **部分出药**：失败储位回写状态 1、数量 loop − 1。这是软件判定完成的包数（包含【马偕定制】路径），失败的那一包是否已经落药需要现场核对。
- **设备异常**：501/503 终结当前储位任务，不发送中止命令；队列中其他储位任务继续执行，并照常回写。
- **设备断线**：发送被静默丢弃，该步骤超时后以 503 终结。
- **中控断网或回写失败**：显示错误消息，不重试、不在本地保存待补传记录，可能出现“已出药、未记账”，必须人工对账。

## Risks / Trade-offs

- [代码、文档、规格三处描述可能不一致] → 规格保持精简并引用文档章节；每个变更的 tasks 都必须包含文档同步（`config.yaml` 规则）。
- [基线有误] → tasks 逐条对照源码核实；场景只写已核实的范围；不确定的内容放入“发现”。
- [中文规格不满足校验格式] → `config.yaml` 规则要求第一行正文包含 SHALL/MUST；tasks 中运行 `openspec validate --strict`。
- [`openspec update` 改动生成文件] → 视作生成产物，更新后统一提交。
- [基线只覆盖 `mj2`] → Impact 和 D6 已写明，移植时不能直接复制。

## 基线梳理中的发现（本变更不修复）

以下结论来自对 `b8e9ebe` 源码的静态阅读，尚未在运行环境中复现。“核实状态”取值：**静态确认**（调用链已逐行核对）、**已复现**、**待现场确认**。

| # | 发现 | 位置 | 可能影响 | 文档现状 | 核实状态 |
| --- | --- | --- | --- | --- | --- |
| F1 | 储位计划量大于现存量时，该储位任务的开始回调会显示「…無法繼續領藥!」并**提前启用「確定」**，而出药仍在继续。操作人员此时关闭窗口会把计划置空。 | `DlgGeneralProviding.setPageItems`、`update`、`closeWindow` | 若这是最后一个任务：回写时空指针被捕获，**结果不再回写中控**，但设备可能已经出药。若后面还有任务：下一个开始回调抛出的空指针没有被捕获，**请求队列线程终止**，之后所有出药都无法执行，直到重启。 | 文档只提到“提示不阻断入队”，没有写出这一后果 | 静态确认；后果待现场确认 |
| F2 | `connect()` 在连接器已活动时会关闭所有会话并 **dispose 共享连接器**，随后在已 dispose 的连接器上再次连接；首次连接只捕获 `RuntimeIoException`。 | `SignageIFClient.connect` | 配置多组端口时，药品柜连接可能永久不可用。 | 文档只写了“不是多柜隔离” | 静态确认（含 MINA 2.1.4 字节码：连接器已 dispose 后 `connect()` 抛 `IllegalStateException`）；待现场确认 |
| F3 | 周期保活线程只创建一次，持有首个会话的引用。 | `KeepConnectionFilter.sessionOpened` | 重连后的新会话收不到周期保活；影响取决于控制器是否依赖保活。 | 已写在第 4.2 节，但没有写出后果 | 静态确认；对控制器的影响待现场确认 |
| F4 | STEP4 收到 10363 时的重新计时条件 `(F & 0x01) == 0x02` 永远不成立，疑似笔误。 | `Vendout` STEP4 | 贩卖报告不会延长 30 秒等待，影响较小。 | 已写在第 5.3 节 | 静态确认（`F & 0x01` 只能为 0 或 1，条件恒不成立） |
| F5 | 开始、进度和最终回调都在 try/catch 之外，请求队列循环也没有捕获异常。 | `Vendout.start`、`RequestQueue.init` | 界面回调中的任何异常都会终止出药线程（F1 只是其中一种触发方式）。 | 已写在第 6.1 节 | 静态确认 |
| F6 | 遇到非法转义或长度不符时，解码器复位并等待更多数据，不会跳过坏帧。 | `SignageIFDecoder` | 一个坏帧可能阻塞后续所有报文，直到重连。 | 已写在第 4.4 节（坏帧重同步） | 静态确认（依赖 MINA 累积解码在返回 false 时保留未消费数据的语义）；待现场确认 |

建议优先处理 F1，因为它直接影响管制药品的记账；其次是 F2 和 F5。

## Migration Plan

只新增文档和配置文件，不影响构建产物、部署或运行。回滚只需 `git revert` 对应提交；如果已经归档，主规格目录会随提交一起回滚。

## Open Questions

- 团队实际使用哪些 AI 工具？由此决定是否保留 `.claude/`、`.cursor/`。
- 是否立即为 F1（以及 F2、F5）立项修复？
- 是否有测试环境，可以确认 F1–F3 以及 `cabinet-protocol` 中的重连和保活行为？
- 后续是否需要在 `ghnz` 引入 OpenSpec？
