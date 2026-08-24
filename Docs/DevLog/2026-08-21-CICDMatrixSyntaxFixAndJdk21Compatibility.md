# CICD 工作流排障复盘：Matrix 语法修复与 JDK 21 兼容性问题

> 日期：2026-08-21
> 作者：BeeAgent
> 关联 PR：[#161](https://github.com/ACANX/AUtil/pull/161)（fix/CICD-matrix-syntax → V1.3.x）
> 关联 Issue：#159

---

## 一、背景

V1.3.x 分支合并 CICD.yml（由 #160 引入）后，GitHub Actions 报错：

```
Invalid workflow file: .github/workflows/CICD.yml#L1
(Line: 48, Col: 5): Unexpected value 'matrix'
```

即 workflow 文件本身无法通过 GitHub Actions 的 YAML schema 校验，CI 完全无法运行。

## 二、排障过程与修复链

### 2.1 CICD.yml workflow 修复

| # | 问题 | 根因 | 修复 |
|---|------|------|------|
| 1 | `Unexpected value 'matrix'` | matrix 使用动态单值表达式 `matrix: java-version: ${{ github.event.inputs.javaVersion \|\| '25' }}`，不被 GitHub Actions schema 认可 | 改用标准 `strategy.fail-fast: false` + `matrix.include`（JDK 25 + JDK 21） |
| 2 | 排障困难 | compile 步骤带 `-q`，Maven 错误被吞 | 移除 `-q` 保留完整编译输出 |

**补充：** workflow_dispatch 手动选择 JDK 时，通过 `SkipUnselectedJdk` step（`exit 80`）跳过不需要的矩阵组合；push/PR 场景两个 JDK 版本并行测试。

### 2.2 Maven Wrapper 环境修复

| # | 问题 | 根因 | 修复 |
|---|------|------|------|
| 3 | `./mvnw: Permission denied` (exit 126) | `mvnw` 在 git 索引中权限为 100644（无执行位） | 提交权限位变更 100644 → 100755 |
| 4 | `Could not find or load main class MavenWrapperMain` | `.mvn/wrapper/` 目录被 `.gitignore` 忽略，CI checkout 后缺少 `maven-wrapper.jar` 与 `maven-wrapper.properties` | `git add -f` 强制提交 wrapper 文件（takari 0.4.2） |

### 2.3 存量测试 bug（CI 干净环境首次暴露）

| # | 问题 | 根因 | 修复 |
|---|------|------|------|
| 5 | `ZipUtilTest.zipFiles` NoSuchFileException | `savePath` 拼接 `tmpDir + "Zip"` 缺少 `File.separator`，实际写入 `/tmpZip` 而非 `/tmp/Zip` | 两处拼接补上 `File.separator` |
| 6 | `ZipUtilTest` 相对路径解析失败 | `url.getPath().substring(1)` 去掉开头的 `/`，路径变相对路径，CI 工作目录下不存在 | 移除 `substring(1)`，直接用绝对路径 |

> 说明：本地开发机因残留 `/tmpZip` 目录或工作目录巧合能通过，CI 干净环境必挂。

### 2.4 JDK 21 编译兼容性修复（核心难点）

> **关键背景：** 项目 pom 声明 `java.version=21`（编译目标），但实际开发/发布环境使用 JDK 25。JDK 25 的 javac 对模块访问与 ServiceLoader provider 校验宽松，JDK 21 严格，导致大量存量问题仅在 JDK 21 下暴露。

| # | 问题 | 根因 | 修复 |
|---|------|------|------|
| 7 | `annotation-processor` 编译失败：`Provider com.acanx.util.annotation.processor.CopierProcessor not found` | javac 编译时通过 `META-INF/services/javax.annotation.processing.Processor` 发现并实例化 CopierProcessor，其依赖的 annotation-api 类在编译 classpath 缺失 | 该模块加 `<proc>none</proc>`（处理器提供者编译自身无需触发注解处理） |
| 8 | `annotation-processor-ast` 编译失败：`--add-opens has no effect at compile time` | pom 将 JVM 运行期参数 `--add-opens` 配到 `compilerArgs`，javac 编译期不接受 | 移除编译期 `--add-opens`，保留 `--add-exports` |
| 9 | `annotation-processor-ast` fork 编译失败且无错误输出 | fork 模式下 processor 在 forked JVM 中运行，需 JVM 级 `--add-exports` 才能访问 javac 内部 API | 恢复 `<fork>true</fork>` + `<jvmArgs>` 传 `--add-exports`（JVM 级） |
| 10 | 各业务模块 testCompile 失败：`IllegalAccessError: module jdk.compiler does not export com.sun.tools.javac.processing` | javac 自动发现 classpath 上的 annotation-processor（services 声明），实例化 `CopierAstProcessor` 触发内部 API 访问 | `autil-core` + 4 个 json 模块（json-core/fastjson/jackson/gson）加 `<proc>none</proc>`，禁用注解处理自动发现 |
| 11 | `Non-parseable POM: in comment after two dashes (--)` | 自己加的 XML 注释中含 `--`（`--add-exports`），XML 注释禁止连续两个连字符 | 注释中改写为 `add-exports` |

## 三、最终验证结果

```
BuildAndTest (21)  | success ✅
BuildAndTest (25)  | success ✅
QualityGate        | success ✅
BuildArtifacts     | skipped（PR 场景，正常）
```

## 四、关键教训

1. **JDK 版本差异是隐形炸弹：** JDK 21 javac 严格校验模块访问（`--add-exports`/`--add-opens`）与 ServiceLoader provider 实例化，JDK 25 宽松。CI 矩阵覆盖 JDK 21 才能暴露存量问题。
2. **注解处理器模块会污染依赖方：** 含 `META-INF/services` 声明的 processor 模块，任何把它放进编译 classpath 的模块都会被 javac 自动实例化该 processor。要么 `<proc>none</proc>`，要么显式 `-processorpath`。
3. **fork 编译模式的坑：** `maven-compiler-plugin` 的 `<fork>true</fork>` 模式下 javac 错误不显示；且 processor 运行需要 JVM 级 `--add-exports`（配 `<jvmArgs>`），仅配 `compilerArgs` 不够。
4. **CI 干净环境暴露存量测试假设：** 本地能过、CI 必挂的测试，通常是路径拼接、临时目录假设、相对路径解析问题。
5. **XML 注释红线：** XML 注释中禁止出现连续两个连字符 `--`，涉及 `--add-exports`/`--add-opens` 等参数时需改写。
6. **Maven Wrapper 完整性：** `mvnw` 脚本 + `.mvn/wrapper/`（jar + properties）需一并提交，且 `mvnw` 需可执行权限位（100755）。
7. **排障工具链：** 遇到 Maven 错误被吞时，先去掉 `-q`/fork 拿到真实错误，再定位根因。

## 五、涉及文件清单

| 文件 | 变更 |
|------|------|
| `.github/workflows/CICD.yml` | matrix 修复、移除 -q |
| `mvnw` | 权限位 100644 → 100755 |
| `.mvn/wrapper/maven-wrapper.jar` | 新增（git add -f） |
| `.mvn/wrapper/maven-wrapper.properties` | 新增（git add -f） |
| `autil-core/src/test/java/com/acanx/util/ZipUtilTest.java` | 路径拼接 + 绝对路径修复 |
| `annotation/annotation-processor/pom.xml` | `<proc>none</proc>` |
| `annotation/annotation-processor-ast/pom.xml` | 移除 --add-opens、恢复 fork + jvmArgs、`<proc>none</proc>`、注释修复 |
| `autil-core/pom.xml` | `<proc>none</proc>` |
| `autil-json/json-core/pom.xml` | `<proc>none</proc>` |
| `autil-json/json-fastjson/pom.xml` | `<proc>none</proc>` |
| `autil-json/json-jackson/pom.xml` | `<proc>none</proc>` |
| `autil-json/json-gson/pom.xml` | `<proc>none</proc>` |
