# Java 测试代码开发规范

本文档是 AUtil 项目 Java 测试代码（单元测试/契约测试）的开发规范，**强制**执行。
违反本规范属于代码评审红线，合并前必须修正。

## 一、测试方法命名（强制红线）

### 规则

**Java 单元测试方法名中禁止使用简体中文汉字，必须使用英文。**

- 方法名使用英文小驼峰（lowerCamelCase），语义清晰即可
- 测试场景的中文描述写在方法体内注释或断言 message 中，不进入方法名
- JUnit 方法名无外部引用，改名不影响任何调用方

### 正例

```java
@Test
void prettyFormatLineEndingsAlwaysLf() { ... }

@Test
void serializePrettyIndent2() { ... }

@Test
void deserializeUnknownFieldFailThrow() { ... }
```

### 反例（禁止）

```java
@Test
void toJSONStringPrettyFormat换行符固定LF() { ... }   // ❌ 含中文

@Test
void serializePretty缩进2() { ... }                    // ❌ 含中文

@Test
void 空配置未设置项为null() { ... }                    // ❌ 含中文
```

### 背景与教训

- 2026-08-24：跨平台差异修复中再次出现中文测试方法名（契约测试基类），违反此前约定，被 ACANX 严厉批评
- 此前 00ad9cd 提交已确立"测试方法名改为英文"，本次为再犯，故升级为明令禁止的强制规范
- 存量违规（截至 2026-09-11）：20 个中文测试方法名（6 个测试文件），需逐步清理；`JacksonModeTest` 已清理完毕
  - `json-core`：`JSONConfigTest`（7）、`AbstractSerializationContractTest`（5）
  - `json-jackson`：`JSONProviderTest`（2）、`JacksonSerializationContractTest`（2）、`Jackson3SerializationContractTest`（2）
  - `json-fastjson`：`FastjsonSerializationContractTest`（2）

### 检查方式

```bash
# 在仓库根目录执行，检出含中文的方法名（应无输出）
# 末尾的 \( 用于排除注释中的「void 方法返回空」一类文字（非方法声明）
grep -rnP 'void\s+[A-Za-z0-9_\x{4e00}-\x{9fff}]*[\x{4e00}-\x{9fff}][A-Za-z0-9_\x{4e00}-\x{9fff}]*\s*\(' \
  --include='*.java' . || echo "OK 无违规"
```

（CI/评审时可加该检查；本地 IDE 也可配置命名检查。）

## 二、其他测试规范要点

| 项 | 要求 |
| --- | --- |
| 测试类命名 | 与被测类同名 + `Test` 后缀（如 `JacksonUtilTest`），英文 |
| 断言信息 | 断言 message 用简体中文描述期望行为，便于失败定位 |
| 临时文件 | 用 `@TempDir`，禁止硬编码绝对路径（见 `CrossPlatformSpec.md`） |
| 行尾契约 | 快照断言统一 `\n`，用 `normalizeLineEndings` 双保险（见 `CrossPlatformSpec.md`） |
| 确定性 | 测试不依赖系统时区/区域/换行符等环境差异 |

## 相关链接

- `Docs/DevSpec/CrossPlatformSpec.md`（跨平台红线）
- `AGENTS.md`（项目约定，含测试方法名强制条款）
- 跟踪 ISSUE：#182（跨平台后续项）
