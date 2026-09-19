# 跨平台开发规范（Windows/Linux）

本文档记录本项目的跨平台开发红线，后续代码与测试需遵照执行。
背景复盘：`Docs/DevLog/2026-08-24-CrossPlatformDiffPostmortem.md`（Windows 换行符 `\r\n` 差异 + 路径正则异常导致 json-jackson 测试失败）。

## 红线一：文件路径/用户输入禁止直接拼入正则

### 规则

**禁止**将文件路径或用户输入直接拼入 `replaceFirst` / `replaceAll` / `matches` / `split` 等正则 API 的 pattern。

必须使用：

- `Path` API（`Paths.get(...)`、`relativize`、`endsWith`、`startsWith`）
- 纯字符串处理（`startsWith` + `substring`）
- 或 `Pattern.quote(...)` 转义后再拼入

### 原因

Windows 绝对路径（如 `C:\Users\ACANX\AppData\Local\Temp\...`）含反斜杠 `\`，在正则中为**转义符**。`\U`、`\A` 等属非法转义序列，`Pattern.compile` 直接抛 `PatternSyntaxException`；Linux 路径（`/home/...`）无反斜杠，永不触发——这正是 CI（ubuntu）全绿而 Windows 本地必现的原因。

### 正例

```java
// ✅ Path API 剥离前缀
Path base = Paths.get(basePath);
Path full = Paths.get(fullPath);
String relative = base.relativize(full).toString().replace('\\', '/');

// ✅ 纯字符串处理（先归一化分隔符再剥离）
String normalized = fullPath.replace('\\', '/');
String prefix = basePath.replace('\\', '/');
String relative = normalized.startsWith(prefix)
        ? normalized.substring(prefix.length()) : normalized;

// ✅ Pattern.quote 转义
String pattern = ".*" + Pattern.quote(basePath) + "/?";
```

### 反例

```java
// ❌ Windows 下必抛 PatternSyntaxException
String relative = fullPath.replaceFirst(".*" + basePath + "/?", "");
```

## 红线二：JSON 输出与快照断言统一 `\n` 行尾；文件系统断言避免绝对路径

### 规则

1. **JSON 美化输出（pretty）行尾必须恒为 `\n`**，不得跟随系统 `line.separator`。
   - Jackson 默认缩进器（`DefaultIndenter`）跟随 `line.separator`（Windows 为 `\r\n`），使用 `writerWithDefaultPrettyPrinter()` 或默认 pretty printer 时必须显式指定换行符：`new DefaultIndenter(" ", "\n")`。
   - fastjson2 / Gson 的 pretty 输出硬编码 `\n`，可放心使用，但需契约测试兜底。
2. **断言"文本块期望值"的测试**，被测输出必须保证 `\n`（Java 文本块行终止符恒为 `\n`，与系统无关）。
3. **快照断言统一先归一化再比较**：使用 `AbstractSerializationContractTest.normalizeLineEndings(String)`（`\r\n` → `\n`）作为双保险。
4. **文件系统断言避免绝对路径**：用 `@TempDir` 注入临时目录 + `endsWith` / `relativize` 断言相对关系，禁止硬编码 `C:\...` 或 `/home/...` 等平台相关路径。

### 原因

Windows 下 `line.separator = \r\n`，跟随系统换行符的实现输出每行以 `\r\n` 结尾，与期望 `\n` 字节不一致——控制台显示完全相同（`\r` 不可见），极具迷惑性。只有字节级断言（`assertFalse(json.contains("\r"))`）能在 Windows 上守护契约。

### 正例

```java
// ✅ 固定换行符
printer.indentObjectsWith(new DefaultIndenter(" ".repeat(2), "\n"));
printer.indentArraysWith(new DefaultIndenter(" ", "\n"));

// ✅ 字节级断言
assertFalse(json.contains("\r"), "行尾必须为 \\n，不得出现 \\r\\n");

// ✅ @TempDir + 相对断言
@TempDir
Path tempDir;
Path file = tempDir.resolve("data.json");
assertTrue(file.endsWith("data.json"));
```

### 反例

```java
// ❌ 跟随系统换行符（Windows 输出 \r\n）
mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);

// ❌ 绝对路径断言
assertTrue(new File("D:\\Code\\JavaCode\\AUtil\\target\\data.json").exists());
```

## 相关链接

- 复盘文档：`Docs/DevLog/2026-08-24-CrossPlatformDiffPostmortem.md`
- 跟踪 ISSUE：#182（子任务 #183 ~ #187）
- 行尾基线：`.gitattributes` / `.editorconfig`（入库统一 LF）
