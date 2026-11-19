## Why

项目已有完整的架构、详细设计和操作文档，但需求变更没有结构化的提出、评审和归档流程。例如 `b8e9ebe` 中“STEP4 累计三次 `0x02` 判完成”这项放宽出货成功判定的客户定制，目前只记录在 commit message 和文档里。

OpenSpec 的 `MODIFIED` 增量要求主规格中已有对应的 Requirement。现在为风险最高的出药核心链路建立基线规格，后续变更（包括可能基于本分支移植到其他客户）就能明确写出“改前 → 改后”，而不是只能新增描述。

## What Changes

- 将已初始化的 OpenSpec 脚手架纳入版本控制：`openspec/`（含 `config.yaml` 的项目上下文与产物规则），以及 GitHub Copilot、Claude、Cursor 的 `/opsx` 命令和技能文件。
- 为三个核心能力建立基线规格。规格只描述 `mj2` / `b8e9ebe` 的现有行为，**不修改任何 Java 代码、POM 或运行配置**：
  - 双条码调剂（界面与中控编排）
  - 单储位出药流程（Vendout 四步设备交互）
  - 药品柜 TCP 协议（连接、帧、编解码）
- 在 `docs/README.md` 的维护规则和 `README.md` 的文档导航中登记 `openspec/` 的职责，并在 `CHANGELOG.md` 追加条目。
- 记录基线梳理中发现的、与预期不符的行为（见 design.md），另行立项处理，不在本变更中修复。

非目标：不为登录、初始化、系统参数、库存查看、中控接口等其余能力建立规格；不处理 `ghnz`、`master` 分支。

## Capabilities

### New Capabilities

- `barcode-dispensing`：扫描药袋双条码查询调剂计划，计划显示后自动按储位出药，归集储位结果并回写中控。
- `vendout-flow`：单个储位任务的逐包四步设备交互、两条 STEP4 完成路径（含【马偕定制】）、无进展超时和失败回调，以及任务的串行排队执行。
- `cabinet-protocol`：与药品柜控制器的 TCP 连接、重连与保活、帧结构、转义与长度解析，以及接收分派和命令确认匹配。

### Modified Capabilities

（无。`openspec/specs/` 目前为空。）

## Impact

- **代码基线**：`mj2` / `b8e9ebe`。当前 HEAD `1204e80` 相对该提交只更新了文档。
- **需求来源**：项目维护方，为引入规格驱动的变更管理。
- **客户定制**：本变更本身不是定制，但 `vendout-flow` 基线会用【马偕定制】标注 `b8e9ebe` 的 STEP4 定制行为。
- **三层“成功”**：不改变任何一层。基线规格会明确区分设备流程成功、中控回写成功和实物对账。
- **代码与产物**：无 Java、POM 或配置变更，构建产物和运行行为不变。
- **现场对账与操作手册**：出药行为没有变化，无影响。
- **客户分支**：基线只适用于 `mj2`。`ghnz` 的调剂页与登录页已有大量差异（调剂页约 +463 行），若引入 OpenSpec，需要单独建立基线，不能直接复制本分支的规格。
- **文档与版本控制**：新增 `openspec/` 以及 `.github/`、`.claude/`、`.cursor/` 下由 OpenSpec 生成的文件（可用 `openspec update` 重新生成）；`docs/README.md`、`README.md`、`CHANGELOG.md` 各有小幅更新。
