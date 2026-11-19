## 1. 基线核实（对照 `b8e9ebe` 源码）

- [x] 1.1 逐条核对 `barcode-dispensing` 的 6 条 Requirement 与 `JPMKGeneralProvide`、`DlgGeneralProviding`、`RestfulProvideQuery`、`RestfulProvideResult` 是否一致；不一致时修改规格，或把该项移入 design.md 的“发现”
- [x] 1.2 逐条核对 `vendout-flow` 的 7 条 Requirement 与 `Vendout`、`RequestQueue` 是否一致，重点核对 STEP4 两条完成路径和各步骤的重新计时规则
- [x] 1.3 逐条核对 `cabinet-protocol` 的 8 条 Requirement 与 `SignageIFClient`、`SignageIFEncoder`、`SignageIFDecoder`、`KeepConnectionFilter` 是否一致
- [x] 1.4 在 design.md 的“发现”表中为 F1–F6 标注核实状态：静态确认、已复现或待现场确认

## 2. 文档与约定同步

- [x] 2.1 按 `docs/README.md` 第 6 节的对照表逐行核对：本变更不涉及其中任何一类代码变动，确认 ARCHITECTURE、DETAILED_DESIGN、OPERATIONS 不需要修改
- [x] 2.2 在 `docs/README.md` 第 1 节的文档职责表中登记 `openspec/`（行为规格与变更记录），并在第 6 节新增一行：需求或行为变更先建 OpenSpec 变更，归档后同步 `openspec/specs/` 和对应文档
- [x] 2.3 在 `README.md` 的“文档导航”表和项目结构树中加入 `openspec/`
- [x] 2.4 在 `CHANGELOG.md` 顶部追加条目，沿用“`# YYYY-MM-DD` + `Change Requirement - 马偕针剂版本`”格式，写明已引入 OpenSpec 并为三个核心能力建立基线规格

## 3. 版本控制

- [x] 3.1 确认 `.gitignore` 不会排除 `openspec/`、`.github/`、`.claude/`、`.cursor/` 下需要提交的文件
- [x] 3.2 按 design.md D7 和 Open Questions 的结论确定保留哪些工具目录，把 `openspec/` 与工具生成文件放在一个单独的提交中，便于回滚（结论：三套工具目录全部保留）

## 4. 验证

- [x] 4.1 运行 `openspec validate adopt-openspec-baseline --strict`，结果为 valid
- [x] 4.2 用 JDK 8 运行 `bash mvnw -Dexec.skip=true package`，确认构建成功（本变更不改代码，此步只证明构建不受影响）
- [x] 4.3 在本文件末尾记录验证结论：三个规格中“已验证”的部分为对照源码的静态核实；STEP4 定制路径、重连、保活等设备侧行为标为“待现场确认”
- [ ] 4.4 （可选，需要测试柜或设备模拟器）复现或排除 F1–F3，把结论写回 design.md（未执行：当前没有测试柜或模拟器，F1–F3 仍为“静态确认”）

## 5. 归档准备

- [x] 5.1 定稿三个主规格的 Purpose 文本（如下）；归档同步时用它们替换自动生成的 “TBD”，并运行 `openspec validate --specs --strict`
  - `barcode-dispensing`：描述调剂页通过药袋双条码向中控查询调剂计划、计划显示后自动按储位出药、归集储位结果并回写中控的行为（基线：mj2 / b8e9ebe）。
  - `vendout-flow`：描述单个储位出药任务的串行排队、逐包四步设备交互、STEP4 完成判定（含马偕定制路径）、无进展超时与失败回调（基线：mj2 / b8e9ebe）。
  - `cabinet-protocol`：描述终端与药品柜控制器之间的 TCP 连接、重连与保活、帧结构与转义、长度解析、接收分派与命令确认匹配（基线：mj2 / b8e9ebe）。
  - 状态：已于归档时（2026-11-19）替换三个主规格的 “TBD”；`openspec validate --specs --strict` 3/3 通过。

## 验证记录（2026-11-19）

| 项目 | 结论 | 状态 |
| --- | --- | --- |
| 21 条 Requirement | 逐条对照 `b8e9ebe` 源码核实；修正 `cabinet-protocol` 中 2 处措辞（发送要求会话数恰好为 1，因此改为“两个或更多会话”；接收侧以未转义 0x02 为帧边界，不校验首字节） | 已验证（静态） |
| 设备侧行为（STEP4 两条完成路径、各步骤超时、重连、保活） | 只对照源码，没有连接药品柜或模拟器 | 待现场确认 |
| 中控交互（计划查询、结果回写、结果码） | 只对照源码，没有连接中控 | 待现场确认 |
| `openspec validate adopt-openspec-baseline --strict` | valid | 已验证 |
| 构建 | 用 `git archive HEAD` 导出干净副本，以 JDK 1.8.0_371 运行 `bash mvnw -B -Dexec.skip=true package`：BUILD SUCCESS（29.9 秒）；编译主源码 97 个、测试源码 6 个，测试按 POM 配置跳过；生成 Spring Boot JAR 和 Assembly JAR；6 条 WARNING 均为 `AESUtils` 使用 `sun.misc` | 已验证 |
| 新克隆可用性 | 去掉 git 不跟踪的空目录后，`openspec list` 和 `validate` 仍然正常 | 已验证 |
| F1–F6 | 调用链已静态确认，F2 另经 MINA 2.1.4 字节码确认；均未复现 | 待现场确认 |
