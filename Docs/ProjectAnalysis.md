# AUtil 项目分析

> 本文是对 AUtil 工具库的整体技术分析。分析基准:分支 `feat/V1.3.0/Local`,根 pom `revision = 1.3.0`,`java.version = 21`。
> 所有模块的源码均经实际阅读;关键类、SPI 文件、空壳目录等断言已用 `git`/文件系统交叉校验。

---

## 1. 项目概述

**AUtil(ACANX-Util)** 是 ACANX 开源的 Java 工具库,始于 2019 年,Maven 坐标 `com.acanx.util:autil:1.3.0`,以 Apache-2.0 协议发布至 Maven 中央仓库。

定位:封装字符串、文件、IO、集合、日期时间、HTTP、JSON、properties、IP 地址、URL、线程、操作系统、图像、消息摘要、编解码等横切通用能力,全部以静态工具方法对外暴露。

**分支即 JDK 版本线**:仓库用分支划分对 JDK 版本的支持线,当前 `V1.3.x` 对应 **JDK 21**(根 pom `java.version=21`),另有 `V1.0.x`(JDK8)、`V2.x`(JDK25)等平行线。

**在线文档**:`https://docs.acanx.com/Java/AUtil/index.html`。

---

## 2. 整体架构与模块依赖

AUtil 是一个 Maven 多模块聚合项目,根 POM(`packaging=pom`)声明 5 个一级模块:

```
autil  (根聚合 POM · com.acanx.util:autil:1.3.0 · JDK21)
├─ autil-core ..................... 核心工具集(92 主源文件),零内部依赖,被下游广泛依赖  ★ 基石
├─ autil-json (聚合)
│  ├─ json-core ................... JSON 门面 / SPI 契约层        → autil-core
│  ├─ json-fastjson ............... fastjson2 实现              → autil-core, json-core, model-test
│  ├─ json-gson ................... gson 实现                   → autil-core, json-core, model-test
│  └─ json-jackson ................ jackson 实现                → autil-core, json-core, model-test
├─ annotation (聚合)
│  ├─ annotation-api .............. 注解定义(零内部依赖)        ★ 基石
│  ├─ annotation-processor ........ 注解处理器(JavaPoet 源码生成) → annotation-api
│  └─ annotation-processor-ast .... AST 处理器(javac 内部 API)  → annotation-api
├─ autil-incubator (聚合) —— 孵化/实验性功能区
│  ├─ incubator-core .............. 孵化功能(参与构建)          → autil-core
│  └─ incubator-annotation ........ 已毕业冻结(未参与构建)      → autil-core
└─ autil-test ..................... 注解处理器编译期验证载体      → autil-core, annotation-api, model-test
```

**两个基石**:`autil-core`(工具)与 `annotation-api`(注解)自身无内部依赖,是整个反应堆的底座。依赖方向严格单向,核心模块不反向依赖上层模块。

**外部依赖**:`model-test`(`com.acanx.meta.model:model-test:0.8.9`)不在本反应堆内,仅被 `json-fastjson/gson/jackson` 与 `autil-test` 在测试/样本期引用,提供 `User`、`MessageFlex`、`MessageStable` 等样本模型。

---

## 3. 构建与发布

### 3.1 版本管理

- 采用 `${revision}` 单一版本源(根 pom `revision=1.3.0`)+ `flatten-maven-plugin`(1.8.0,`flattenMode=ossrh`)做 CI-friendly 版本管理,产出可上传中央仓库的扁平 POM。
- `flatten` 绑定 `process-resources`,`flatten.clean` 绑定 `clean`。
- 扁平化产物**不入库**:根 `.gitignore` 已忽略默认产物名 `.flattened-pom.xml`;`annotation/pom.xml` 配置 `<flattenedPomFilename>pom-xml-flattened</flattenedPomFilename>`,被子模块继承,故 annotation 系产物名为 `pom-xml-flattened`,亦已被 `.gitignore` 忽略。

### 3.2 Maven Profiles

根 pom 定义三套 profile:

| profile | 定位 | 备注 |
|---|---|---|
| `dev` | 本地发布 | 含 compiler / flatten / surefire / source / javadoc 插件 |
| `product` | 发布到本地仓库 | 空定义 |
| `sonatype-oss-release` | 发布到 Maven 中央仓库 | **默认激活**(`activeByDefault=true`),含 GPG 签名 + `central-publishing-maven-plugin` |

发布链路:`maven-source-plugin` 产 sources jar → `maven-javadoc-plugin` 产 javadoc jar → `maven-gpg-plugin`(`verify` 阶段)签名 → `central-publishing-maven-plugin` 推送至 Sonatype 中央仓库(`autoPublish=true`)。

### 3.3 依赖版本清单(根 pom properties)

| 依赖 | 版本 | 用途 |
|---|---|---|
| `slf4j-api` | 2.0.18 | 日志门面(provided,仅部分模块) |
| `lombok` | 1.18.46 | 声明版本但核心模块未使用 |
| `fastjson2` | 2.0.64 | json-fastjson |
| `jackson-databind/jsr310` | 2.22.2 | json-jackson |
| `gson` | 2.14.0 | json-gson |
| `junit-jupiter` | 6.1.3 | 测试 |
| `auto-service` | 1.1.1 | 注解处理器注册(provided) |
| `javapoet` | 1.13.0 | 源码生成 |
| `meta-open` | 0.8.9 | 测试样本模型 |

---

## 4. autil-core 模块详解

**定位**:整个工具库的零第三方运行时依赖基础层。仅依赖 `junit-jupiter`(test),对所有第三方库零依赖,可被任意技术栈安全引入。JDK 21,未做 JPMS 模块化。

### 4.1 包结构

```
com.acanx
├── annotation      API 稳定性 / 可空性标注注解(@Alpha/@Beta/@Stable/@Nullable 等)
├── c               常量定义(现行版本,短命名:Const/FileConst/HTTPConst/MimeConst/MetaConst/PatternConst)
├── constant        常量定义(旧版本,整包 @Deprecated)
├── enums           领域枚举
└── util            工具类主体(扁平化按能力域命名 *Util,48 个类)
    ├── file / file.filter   文件容量值对象与 FileFilter 实现
    ├── function             函数式辅助(批处理)
    ├── hardware             硬件信息探测
    ├── http                 结构化 HTTP 客户端(请求/响应/上下文/门面)
    ├── image                图像生成(验证码)
    ├── model.property       .properties 解析值对象
    ├── os                   操作系统识别与各 OS 占位类
    ├── properties           保序保注释的 Properties 实现
    ├── thread               线程工厂
    └── time                 计时器(StopWatch)
```

**双轨过渡约定**:`com.acanx.constant` → `com.acanx.c` 是同一套常量的新旧两版,旧包整包 `@Deprecated`。**一处遗漏**:`constant/HTTPC.java` 未标 `@Deprecated`,而其替代者 `c/HTTPConst.java` 已存在。

### 4.2 API 成熟度自标注体系

`com.acanx.annotation` 定义了稳定性标注注解,供全库自我标注成熟度:

| 注解 | 语义 | 全库使用次数 |
|---|---|---|
| `@Alpha` | 不稳定,可能重构/删除 | **277** |
| `@Beta` | 趋于稳定 | 1 |
| `@Stable` | 稳定 | 0 |
| `@Deprecated` | 已废弃 | 17 |

这意味着**当前几乎全部 API 都自评为 Alpha 阶段**,对使用方是明确的稳定性预警。

### 4.3 核心工具类(按领域)

| 领域 | 代表类 | 说明 |
|---|---|---|
| 字符串 | `StringUtil`(1792 行)、`RandomUtil`、`CharacterUtil`、`BoolUtil` | 模块最大类,含判空/join/命名转换/补齐截取/MD5/SHA1 |
| 集合对象 | `CollectionUtil`、`ArrayUtil`、`MapUtil`、`ObjectUtil`、`EnumUtil` | 多为极简判空分发器;`EnumUtil` 含 15 个枚举反射方法 |
| 日期时间 | `DateTimeUtil`、`DateUtil`、`LocalDateTimeUtil`、`TsUtil`、`IntUtil`、`LongUtil`、`BigIntUtil` 等 | 该领域类最多,**存在明显重叠** |
| 数值 | `DoubleUtil`(18 方法,基于 BigDecimal 精确运算) | 本领域唯一完整实现 |
| IO/文件 | `FileUtil`(871 行)、`IOUtil`、`ZipUtil`、`PropertiesUtil`、`OrderedProperties` | **亮点:`OrderedProperties` 手写解析器,改写后完整保留注释/空行/原始分隔符** |
| HTTP/网络 | `BaseHTTP`(基于 JDK11+ `HttpClient`)、`HRequest`/`HResponse`/`HContext`、`IPUtil`/`IPv4Util`/`IPv6Util`、`URLUtil` | 门面 + 建造者 + 上下文对象 |
| 反射 | `ReflectionUtil`、`TypeUtil` | `TypeUtil.getRawType` 实现完整干净 |
| 编码摘要 | `Base64Util` | 摘要方法挂在 `StringUtil`/`FileUtil` 上,无独立 `Md5Util` |
| 并发计时 | `NamedThreadFactory`、`StopWatch`(移植自 Spring)、`BatchProcessor` | |
| 系统/硬件 | `OSUtil`、`CpuInfo`、`SystemUtil`、`PrintUtil`、`JDBCUtil`、`CaptchaUtil` | `CpuInfo` 优雅降级到 `availableProcessors()` |

### 4.4 API 风格

主体是**静态工具方法 + 私有构造禁实例化**(但该约定只覆盖约 1/4 工具类)。HTTP 领域是模块内唯一的完整面向对象设计:`HRequest.Builder` 组装,`BaseHTTP` 作门面,`HContext` 贯穿(带 `attachment` 扩展槽)。

### 4.5 已知问题

- **无日志门面**:诊断输出用 `System.out.println`/`e.printStackTrace()`,仅 `FileUtil` 用 JDK `java.util.logging`。是全模块最值得改进的一致性问题。
- `DateUtil` 持有 3 个 `public static SimpleDateFormat` 可变静态字段(非线程安全)。
- 摘要方法异常时 `printStackTrace()` 后 `return null`,吞掉异常。
- `NamedThreadFactory` 未 `implements ThreadFactory`,无法用于线程池。
- 约 12 个空占位类(`ClassUtil`/`XmlUtil`/`YamlUtil`/`MathUtil`/`HttpsUtil`/`NetUtil`/日期占位类/os 占位类 5 个)。
- `Md5UtilTest` 测的是 `StringUtil.getStringMD5Code`,`Md5Util` 类不存在。

---

## 5. autil-json 模块群详解

**定位**:一套「JSON 门面 + 多实现」架构,通过 JDK `ServiceLoader` SPI 在编译期/运行期装配具体实现。

### 5.1 四层结构

| 层 | 角色 | 代表类 | 所在包 |
|---|---|---|---|
| L1 调用层 | 静态门面 | `JSONUtil` | `com.acanx.util.json` |
| L2 契约层 | SPI 接口(14 方法,标 `@Alpha`) | `JSONProvider` | `com.acanx.util.json` |
| L3 适配层 | 适配器 | `FastJSONProvider`/`JacksonProvider`/`GsonProvider` | `com.acanx.util.json.impl` |
| L4 实现层 | 库特化工具 | `FastJSON2Util`/`JacksonUtil`/`GsonUtil` | `com.acanx.util.json` |

**依赖方向严格单向**:`json-core` 不依赖任何第三方 JSON 库,可独立发布给下游做编译期依赖;三个实现模块各自依赖 `json-core` + 对应 JSON 库,彼此零耦合。

### 5.2 装配机制

`JSONUtil` 静态代码块内一次性完成:
1. `ServiceLoader.load(JSONProvider.class)` 发现候选;
2. 调用 `isAvailable()`(各实现用 `Class.forName` 探测库是否真实存在)过滤;
3. 按类名关键字仲裁优先级:**fastjson2(3) > jackson(2) > gson(1) > 其他(0)**;
4. 无可用实现抛 `IllegalStateException` 快速失败;
5. 锁定 `static final PROVIDER`,此后不可变。

**切换实现的唯一手段是增删 Maven 依赖**(无运行时开关);自定义 Provider 恒得 0 分,无法覆盖内置实现。

### 5.3 三个实现的取舍差异

| 维度 | fastjson2(默认) | gson | jackson |
|---|---|---|---|
| 日期处理 | 库默认行为 | 最完善(`Iso8601Adapter` 支持 9 种输入格式) | 单一格式 `yyyy-MM-dd'T'HH:mm:ss.SSSSSS` |
| 命名策略 | `SupportSmartMatch` 智能匹配(驼峰/下划线双向兼容,`*Snake` 方法语义弱化) | `LOWER_CASE_WITH_UNDERSCORES`(真下划线) | `SNAKE_CASE`(真下划线) |
| ObjectMapper | — | 每次新建 `Gson` 实例(无共享状态) | 共享静态 `MAPPER` |
| 日志 | — | — | 引入 slf4j-api(仅 `JacksonFileUtil`) |

Gson 与 Jackson 的 `LocalDateTime` 序列化格式(6 位微秒 ISO8601)刻意对齐,降低切换实现时的数据格式漂移。

### 5.4 已知问题

- `JSONUtil.parseObject(json, clazz, config)` 的 `config` 参数**全链路丢弃**(未转发到 Provider),是已声明未实现的扩展点。
- `JacksonUtil` 共享静态 `MAPPER` 被 `parseObjectSnake`/`parseArraySnake` 运行时改写(`registerModule`/`setPropertyNamingStrategy`),`ObjectMapper` 使用中改配置属于误用。
- `FastJSON2Util.toJSONString` 与 `toJSONStringForStorage` 方法体完全相同。
- 装配过程用 `System.out.println` 输出 3 行信息(含 `getPriority` 内的调试残留),无法关闭。
- `JsonFileUtil` 与 `JacksonFileUtil` 近重复实现。
- **测试基本为空壳**:四个模块的 `JSONProviderTest` 均为空方法体,无任何测试验证 SPI 选优或跨实现一致性。

---

## 6. annotation 模块群详解

**定位**:编译期对象拷贝工具,单注解 `@Copier` + 双引擎实现,对标 MapStruct(源码生成)与 Lombok(AST 改写)两条路线。

### 6.1 `@Copier` 注解(annotation-api)

- `@Target(METHOD)` / `@Retention(SOURCE)` —— 仅编译期可见,**零运行时开销、无法反射读取**。
- 约定被注解方法前两参数为 `(src, dest)`,方法体留空由编译期填充/生成。
- 属性:`strategy`(SHALLOW/DEEP)、`ignoreNull`、`exclude`、`include`、`useAccessors`、`generationMode`(ASM/BYTECODE,默认 BYTECODE)。
- `annotation-api` jar 对外零传递依赖(仅 slf4j provided)。

### 6.2 双引擎

| 维度 | annotation-processor | annotation-processor-ast |
|---|---|---|
| 承接模式 | `generationMode = ASM` | `generationMode = BYTECODE` |
| 使用 API | 标准 `javax.lang.model` + **JavaPoet** | 标准 API + **`com.sun.tools.javac.*` 内部 API** |
| 产出 | **新增**辅助类 `XxxCopier`(经 Filer) | **原地改写**被注解方法的方法体,不新增文件 |
| 调用方式 | 需调用生成的辅助类静态方法 | 直接调用原方法,调用方零感知 |
| 编译器兼容性 | 任意 JSR269 编译器(含 ECJ) | **仅 javac** |
| 额外依赖 | javapoet | 无(auto-service provided 但未使用) |

两套处理器都声明支持同一注解,靠 `generationMode` 各自主动跳过对方的活,因此**可安全同时注册在同一次编译中**。

### 6.3 技术亮点

- **AST 侧用 Mirror API 而非反射读注解值**,规避 `SOURCE` 保留注解 + 处理器独立 ClassLoader 导致 `getAnnotation()` 失效的经典坑。
- `treeMaker.at(pos)` 先定位再造节点,让注入语句携带合法源位置。
- **JPMS 破封装配置成体系**:pom `compilerArgs` + javadoc `additionalJOptions` + `.mvn/jvm.config` 三处覆盖 `--add-exports/--add-opens jdk.compiler/*`。
- 注册方式是**手写 SPI 文件**(非 `@AutoService`);`auto-service` 是已引入未使用的依赖。

### 6.4 消费方

`autil-test` 的 pom 将 `CopierAstProcessor` 配为自身编译的注解处理器(`annotationProcessors` + `annotationProcessorPaths` + `fork=true` + `-J--add-exports/--add-opens`)。注意 `autil-test` 只挂了 AST 处理器,`ASM` 路线当前未被集成验证。

### 6.5 已知问题

- `GenerationMode.ASM` 命名与实现不符(未用 ASM 库,实为 JavaPoet)。
- `@CopierConfig` 无任何处理器消费,且默认值与 `@Copier` 不一致。
- ASM 侧 `useAccessors=false` 生成非法代码;`ignoreNull=true` 遇基本类型字段会编译失败;`DEEP` 模式硬编码 `copyTo`,路线未完成。
- AST 侧 `ignoreNull` 读取后未使用(恒等于 true);`strategy`/`DEEP` 未读取;`Set` 字段被强塞 `ArrayList` 类型不匹配。
- `annotation-processor/target/` 为上一代 handler 架构残留,与当前源码不一致。

---

## 7. autil-incubator 与 autil-test 模块

### 7.1 autil-incubator:孵化区,毕业机制真实运转过

定位:新功能孵化区,有两次可验证的「毕业」记录:
1. **工具类毕业**:`EnumUtil`、`PrintUtil` 从 `incubator-core` 迁到 `autil-core`。
2. **注解体系毕业**:`incubator-annotation` 注解体系毕业至顶层 `annotation` 模块群(同一提交建新群 + 注释旧模块)。

**实际参与构建的子模块只有一个 `incubator-core`**:`incubator-annotation` 已从 `<modules>` 注释移除。

| 子模块 | 在 `<modules>` | 现状 |
|---|---|---|
| `incubator-core` | 是 | 2 主类(均 `@Alpha`):`StringAlignUtil`(控制台宽度/对齐)、`TablePrinter`(ASCII 表格) |
| `incubator-annotation` | 否(注释) | 已毕业冻结,`ObjectCopyProcessor` 整文件注释,测试失效 |
| `annotation-processor` | 否 | **空壳**:无 pom、无 src,git 仅跟踪未提交的 `.gitignore`,只剩 0.1.1.x 版本陈旧 target |
| `incubator-test` | 否 | **空壳**:无 pom、无 src 目录,同上 |

### 7.2 incubator-core 的不一致

- `pom.xml` 将 `<java.version>` 覆写为 **11**(根 pom 与其余模块为 21),是有意兼容还是漏改待确认。
- `PrintUtilTest` 引用的 `PrintUtil` 已迁出至 `autil-core`,测试留在了 incubator-core 未同步迁移。
- 3 个测试均无断言(靠 `System.out` 目视验证)。
- README 内容仅 `# XXX`(占位未写)。

### 7.3 autil-test:注解处理器编译期验证载体

**定位:不是给使用方的测试工具库,而是项目内部用于「编译自己以跑通注解处理器」的验证/示例模块。** 判据:pom 把自身编译配成处理器测试床(指定 `CopierAstProcessor` + processorPaths),不导出任何测试工具,无任何模块依赖它(依赖图叶子)。

主源码 5 个类均为 `@Copier` 样本:`TestClass`(各配置组合)、`Person`/`Address`(样本 POJO + 手写参照实现)、`Copy2`(跨外部模型)、`DayEnum`(供 `EnumUtil` 验证)。

---

## 8. 工程规范与协作约定

详见 [`AGENTS.md`](../AGENTS.md) 与 [`Docs/DevSpec/GitCommitSpec.md`](DevSpec/GitCommitSpec.md),要点:

- **语言**:未特别说明一律使用简体中文(对话、文档、注释、图表标注);代码标识符、提交 type 前缀保持英文。
- **文件命名**:新建 Markdown/配图采用大驼峰(PascalCase);沿用生态惯例的固定名文件不改写。
- **文档目录**:`Docs/` 统一存放,`Docs/DevSpec/` 放开发规范,`Docs/Asset/` 放资产。
- **双远端**:`github`(主远端/上游)、`gitee`(备份)。不要用 `git fetch --prune`(两远端共用 `origin/*` 命名空间,会误删分支引用);需确定某远端真实状态用 `git ls-remote <remote>`。
- **大小写改名**:仅大小写变化时必须拆成两个中转提交,否则他人在大小写不敏感文件系统上 checkout 会冲突。
- **Dependabot**:配置在 `.github/dependabot.yml`,含 maven 与 github-actions 两类更新,默认 daily 调度(附 weekly 备用写法便于切换),目标分支 `dependa`。

---

## 9. 成熟度评估

| 模块 | 成熟度 | 依据 |
|---|---|---|
| `autil-core` | 稳定 | 被所有模块依赖,零外部业务依赖,工具类最多(但 `@Alpha` 标注密集) |
| `annotation-api` + 两个处理器 | 当前主线,已成型仍在打磨 | 三模块全部启用,`autil-test` 编译期真实调用处理器 |
| `incubator-core` | 试验性可用 | 参与构建可编译,但两类均 `@Alpha`,测试无断言 |
| `incubator-annotation` | 已毕业冻结,事实废弃 | 已移出 `<modules>`,测试失效,旧实现残留 |
| `annotation-processor` / `incubator-test`(空壳) | 构建残留 | 无 pom 无源码,仅陈旧 target |
| `autil-test` | 可用的验证载体,内部待整理 | 处理器链路配置完整,但组织松散 |

---

## 附录:关键文件索引

- 根 pom:`pom.xml`(`revision=1.3.0`、`java.version=21`)
- 项目约定:`AGENTS.md`
- 提交规范:`Docs/DevSpec/GitCommitSpec.md`
- Dependabot:`.github/dependabot.yml`
- 核心模块:`autil-core/src/main/java/com/acanx/util/`、`autil-core/src/main/java/com/acanx/annotation/`
- JSON 模块:`autil-json/json-core/src/main/java/com/acanx/util/json/`
- 注解模块:`annotation/annotation-api/src/main/java/com/acanx/util/annotation/`
- 孵化模块:`autil-incubator/incubator-core/src/main/java/com/acanx/util/print/`
