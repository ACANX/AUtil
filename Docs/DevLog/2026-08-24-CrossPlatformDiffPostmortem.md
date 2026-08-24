# 复盘：Windows/Linux 跨平台差异导致 json-jackson 测试失败（换行符 + 路径正则）

> 日期：2026-08-24
> 作者：BeeAgent
> 适用范围：`autil-json/json-jackson` 模块（行为快照测试网，见 `Docs/DevProposal/Jackson3Migration.md` 阶段一/二）
> 触发环境：Windows 11 + JDK 21 + Maven（`mvn test`）；Linux CI（ubuntu-latest）同期全绿
> 关联文档：`Docs/DevProposal/Jackson3Migration.md`、`Docs/DevLog/2026-08-21-CICDMatrixSyntaxFixAndJdk21Compatibility.md`
> 关联提交：本次修复尚未提交（待确认）

---

## 一、背景

`json-jackson` 模块的行为快照测试（Jackson 2 / Jackson 3 双 Provider 对照）在 Linux CI 上全绿，
但在 **Windows 本地环境**首次执行 `mvn test` 时出现 **4 失败 + 1 错误**（共 103 个测试）。

**环节排查结论**：Windows 下**编译（compile/testCompile）与打包（package）均正常**，
本次差异全部集中在**测试执行环节**——即"编译打包测试"三环节中仅测试环节暴露跨平台差异，
但根因（换行符、路径分隔符）同样潜伏在产物输出与文件处理逻辑中，属同源风险。

两类问题均属 **Windows 特有、Linux 永不触发** 的跨平台差异：

| # | 测试 | 类型 | 现象 |
|---|------|------|------|
| 1 | `JacksonUtilTest.toJSONStringPrettyFormat` | 失败 | expected 与 but was **显示完全相同** |
| 2 | `Jackson3UtilTest.toJSONStringPrettyFormat` | 失败 | 同上 |
| 3 | `JacksonProviderTest.toJSONStringPrettyFormat` | 失败 | 同上 |
| 4 | `Jackson3ProviderTest.toJSONStringPrettyFormat` | 失败 | 同上 |
| 5 | `JacksonFileUtilTest.findJsonFilesInDirectory` | 错误 | `PatternSyntaxException: Illegal/unsupported escape sequence` |

## 二、问题现象

### 2.1 换行符差异（4 个失败，具有强迷惑性）

```
org.opentest4j.AssertionFailedError:
expected: <{
  "user_id" : 11,
  "user_name" : "Alice",
  ...
}> but was: <{
  "user_id" : 11,
  "user_name" : "Alice",
  ...
}>
```

**expected 与 but was 在控制台/日志中逐字相同**——差异是每行行尾的 `\r`（回车符），
在终端与多数日志查看器中不可见，极易误判为"断言魔法失效"或 JUnit 输出 bug。

### 2.2 Windows 路径正则（1 个错误）

```
java.util.regex.PatternSyntaxException:
Illegal/unsupported escape sequence near index 5
.*C:\Users\ACANX\AppData\Local\Temp\junit-18126829904330463025/?
     ^
    at java.base/java.util.regex.Pattern.escape(Pattern.java:2785)
    at com.acanx.util.json.JacksonFileUtil.findJsonFilesInDirectory(JacksonFileUtil.java:110)
```

## 三、根因分析

### 3.1 换行符差异：Jackson 默认缩进器跟随系统 `line.separator`

- Jackson 2（`com.fasterxml.jackson.core.util.DefaultIndenter`）与 Jackson 3（`tools.jackson.core.util.DefaultIndenter`）
  的默认换行符均来自 `System.getProperty("line.separator")`（已通过 jar 字节码验证：
  `jackson-core 2.22.2` / `jackson-core 3.2.2` 的 `DefaultIndenter.class` 常量池均含 `line.separator`）。
- `mapper.writerWithDefaultPrettyPrinter()` 使用的正是默认缩进器：
  - Windows：`line.separator = \r\n` → 美化输出每行以 `\r\n` 结尾；
  - Linux/macOS：`line.separator = \n` → 与期望一致，CI 永不暴露。
- 测试期望值使用 **Java 文本块**（text block），按 JLS 规范其行终止符**恒为 `\n`**，与系统无关。
- 因此断言"看起来一样、字节不一样"：期望 `\n`，实际 `\r\n`。
- **影响面不止 4 个测试**：`JacksonFileUtil.writeArrayToLocalFile`、`serialize(..., OutputFormat.PRETTY)`
  同样经 `writerWithDefaultPrettyPrinter()` 输出，在 Windows 上写入文件/接口响应时行尾均为 `\r\n`，
  属于未暴露的同源隐患（本次测试仅因断言宽松未检出）。

### 3.2 Windows 路径正则：文件路径被拼进正则表达式

- `findJsonFilesInDirectory` 通过 `replaceFirst(".*" + basePath + "/?", "")` 剥离 basePath 前缀生成相对路径。
- Windows 绝对路径（如 `C:\Users\ACANX\...`）含反斜杠，在正则中为**转义符**：
  `\U`、`\A` 等属非法转义序列，`Pattern.compile` 直接抛 `PatternSyntaxException`。
- Linux 路径（`/home/...`）无反斜杠，永不触发——这也是 CI 全绿的第二个原因。

### 3.3 为什么 CI 从未暴露

`CICD.yml` 的 `build-compile` / `unit-test` 两个 job **全部运行在 `ubuntu-latest`**，
矩阵只覆盖 JDK（25 / 21），**未覆盖操作系统**：

| 差异维度 | Windows | Linux（CI 现状） |
|---|---|---|
| `line.separator` | `\r\n` | `\n` |
| 路径分隔符 | `\` | `/` |
| 正则反斜杠陷阱 | 必现 | 永不触发 |
| 大小写敏感性 | 不敏感 | 敏感 |

## 四、修复内容（已实施）

| 文件 | 修复 |
|------|------|
| `JacksonUtil.java`（Jackson 2） | `toJSONStringPrettyFormat` 由 `writerWithDefaultPrettyPrinter()` 改为 `writer(createPrettyPrinter(2))`（换行符固定 `\n`）；`createPrettyPrinter(int)` 补充 `indentArraysWith(new DefaultIndenter(" ", "\n"))`，数组缩进保持默认单空格、仅固定换行符 |
| `Jackson3Util.java`（Jackson 3） | 同上：`writer().with(createPrettyPrinter(2))`，`createPrettyPrinter(int)` 补充数组缩进固定 `\n` |
| `JacksonFileUtil.java` | `findJsonFilesInDirectory` 弃用 `replaceFirst` 正则，改为纯字符串处理：分隔符统一归一化为 `/` 后 `startsWith` + `substring` 剥离 basePath 前缀 |

修复要点：

- **复用既有约定**：两个 Util 本已有固定 `"\n"` 的 `createPrettyPrinter(int)`（供 `OutputFormat.PRETTY` 使用），
  本次让 `toJSONStringPrettyFormat` 与其对齐，输出跨平台一致，不再依赖系统换行符。
- **最小行为变更**：对象缩进仍为 2 空格、数组缩进仍为 1 空格，仅换行符从"系统相关"变为"恒为 `\n`"；
  `JacksonSerializationContractTest`（缩进 4 断言）与 `JacksonFileUtilTest.writeArrayToLocalFile`（`contains("\n")` 断言）均不受影响。

## 五、验证结果

Windows 本机 `mvnw -pl autil-json/json-jackson test`：

```
修复前：Tests run: 103, Failures: 4, Errors: 1
修复后：Tests run: 103, Failures: 0, Errors: 0, Skipped: 0  ✅ BUILD SUCCESS
```

## 六、关键教训

1. **"显示相同"的断言失败先查字节**：`\r` 不可见，expected/but was 逐字相同 ≠ 断言无效。
   遇到此类失败应优先做字节级对比（如 `repr`/十六进制），而不是怀疑框架。
2. **Java 文本块恒为 `\n`**：凡是断言"文本块期望值"的测试，被测输出必须保证 `\n`，
   任何跟随系统换行符的实现（Jackson 默认缩进器、`System.lineSeparator()` 拼接等）在 Windows 上必挂。
3. **文件路径禁止拼进正则**：`replaceFirst`/`replaceAll`/`matches` 的 pattern 中出现
   Windows 绝对路径（含 `\`）必然 `PatternSyntaxException`。路径处理一律用
   `Path` API / `startsWith` + `substring` / `Pattern.quote()`。
4. **只有 Linux 的 CI 无法守护 Windows 契约**：换行符、路径分隔符、大小写、编码等
   Windows 特有差异需要 Windows runner 或约定性测试（字节级断言）才能兜底。
5. **快照测试锁定的是"契约"而非"本机行为"**：行为快照的期望值应取跨平台一致的语义
   （如 JSON 输出统一 `\n`），而不是某台机器的偶然输出。

## 七、后续规避方案（跟踪项）

> 状态说明：以下为待评审/待实施的方案建议，需 ACANX 决策后逐项落地。
> 建议优先级：P0（治本，尽快）> P1（低成本高收益）> P2（规范沉淀）。

### 7.1 P0-1：CI 增加 Windows runner（治本）

| 项 | 内容 |
|---|---|
| 方案 | `CICD.yml` 的 `build-compile` / `unit-test` 矩阵增加 `os: [ubuntu-latest, windows-latest]` 维度（参考 2026-08-21 复盘的 matrix 经验，`fail-fast: false`） |
| 成本 | runner 分钟数约翻倍；可折中为**仅 `unit-test` 加 Windows**（compile 保持 ubuntu），成本约 +50% |
| 收益 | 任何 Windows 特有差异（换行符、路径、编码、大小写）在 PR 阶段直接暴露，无需等本地复现 |
| 风险 | 无；测试类文件均为 UTF-8，surefire 报告上传路径不依赖 OS |
| 状态 | ⏳ 待决策（评估 runner 成本与收益） |

### 7.2 P0-2：全模块审计 pretty 输出换行符（消除同源隐患）

| 项 | 内容 |
|---|---|
| 方案 | 审计 `json-fastjson`（`FastJSON2Util.toJSONStringPrettyFormat`）、`json-gson`（`GsonUtil.toJSONStringPrettyFormat`）及 `json-core` 门面的美化输出换行符，确认或统一为 `\n`；`json-core` 的 `AbstractSerializationContractTest` 增加字节级换行断言（`assertFalse(json.contains("\r"))`） |
| 收益 | 杜绝同类问题在其它 Provider 上复发；契约测试对所有 Provider 生效 |
| 状态 | ⏳ 待实施 |

### 7.3 P1-1：仓库行尾基线固化（.gitattributes / .editorconfig）

| 项 | 内容 |
|---|---|
| 现状 | 仓库**无** `.gitattributes`、`.editorconfig`，行尾完全依赖开发机/IDE 默认，存在文件级漂移风险 |
| 方案 | 新增 `.gitattributes`：`*.java text eol=lf`（含 `*.md`、`*.xml`、`*.yml`），提交时统一入库行尾为 LF；可选补 `.editorconfig`（`end_of_line = lf`） |
| 注意 | 涉及存量文件的批量行尾归一化会产生大 diff，需单独提交、分批推进（参照 GitCommitSpec 中转名提交思路，避免噪音混入功能提交） |
| 状态 | ⏳ 待决策（是否接受存量文件归一化 diff） |

### 7.4 P1-2：测试断言规范化工具

| 项 | 内容 |
|---|---|
| 方案 | 在 `json-core` 测试基类提供 `normalizeLineEndings(String)` 辅助（`\r\n` → `\n`），快照断言统一先归一化再比较，作为实现层修复之外的**双保险** |
| 说明 | 本次已从实现层保证 `\n`，该工具用于防御未来新增实现/第三方输出回归 |
| 状态 | ⏳ 待实施（可选） |

### 7.5 P2-1：规范沉淀（Docs/DevSpec）

| 项 | 内容 |
|---|---|
| 方案 | 在开发规范中新增两条红线：① 文件路径/用户输入禁止直接拼入正则（用 `Path`/`Pattern.quote`）；② JSON 输出与快照断言统一 `\n` 行尾、文件系统断言避免绝对路径（用 `@TempDir` + `endsWith`/`relativize`） |
| 状态 | ⏳ 待实施 |

### 7.6 跟踪项汇总表

| 编号 | 事项 | 优先级 | 状态 | 负责人 |
|------|------|--------|------|--------|
| T-1 | CI 矩阵增加 Windows runner（至少 unit-test） | P0 | ⏳ 待决策 | ACANX |
| T-2 | 全模块审计 pretty 输出换行符 + 契约测试字节级断言 | P0 | ⏳ 待实施 | — |
| T-3 | 新增 `.gitattributes` / `.editorconfig` 固化 LF | P1 | ⏳ 待决策 | ACANX |
| T-4 | 测试断言归一化辅助工具（双保险） | P1 | ⏳ 待实施（可选） | — |
| T-5 | DevSpec 沉淀路径正则与行尾规范 | P2 | ⏳ 待实施 | — |

## 八、涉及文件清单

| 文件 | 变更 |
|------|------|
| `autil-json/json-jackson/src/main/java/com/acanx/util/json/JacksonUtil.java` | `toJSONStringPrettyFormat` 固定 `\n`；`createPrettyPrinter` 数组缩进固定 `\n` |
| `autil-json/json-jackson/src/main/java/com/acanx/util/json/Jackson3Util.java` | 同上（Jackson 3 实现） |
| `autil-json/json-jackson/src/main/java/com/acanx/util/json/JacksonFileUtil.java` | `findJsonFilesInDirectory` 路径剥离改纯字符串处理 |
| `Docs/DevLog/2026-08-24-CrossPlatformDiffPostmortem.md` | 本文档（新增） |
