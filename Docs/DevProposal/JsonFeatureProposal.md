# 框架无关的 JSON-SPI 序列化/反序列化 Feature 特性规范提案

> 状态:待评审
> 作者:ACANX
> 日期:2026-08-21
> 适用范围:`autil-json` 模块群(`json-core` SPI 契约、`json-jackson` / `json-gson` / `json-fastjson` 三实现)
> 关联提案:`Docs/DevProposal/Jackson3Migration.md`

---

## 1. 需求清单

> 本提案的所有需求在此分条列出,作为提案的目标与验收依据。**后续新增的需求在此节增量补充**(新增条目编号顺延),保持清单始终是最新完整的需求全集。
> 编号规则:`S` = 序列化需求,`D` = 反序列化需求,`A` = 注解需求,`E` = 扩展性需求。每条需求可在正文找到对应实现章节。

### 1.1 序列化需求(S)

- **[S-01] 字段命名风格可配置**:支持序列化采用大驼峰、小驼峰、下划线三种命名风格(→ §5.1)。
- **[S-02] 输出格式与缩进可配置**:支持格式化(缩进 2 / 4 / 其他整数值)与紧凑输出;紧凑格式去除不必要的空格与换行(→ §5.2)。
- **[S-03] null 值字段策略可配置**:支持「输出 null」「null 字段不序列化」「null 字段序列化时抛异常」三种策略(→ §5.3)。
- **[S-04] 日期时间 / JDK 内置对象自定义格式化**:支持通过框架自定义注解配置字段序列化格式,且跨 jackson/gson/fastjson 三框架统一支持(→ §5.4)。
- **[S-05] 补充序列化 Feature**:枚举序列化方式、Map 键排序、类型信息写入、空 Bean 处理、HTML/Unicode 转义、循环引用检测、字段排序等,凡三框架都支持的纳入通用 Feature(→ §5.5、§9.1)。

### 1.2 反序列化需求(D)

- **[D-01] 字段映射规则可配置**:支持智能映射、仅同名字段映射、下划线字段名 → 小驼峰 Java 对象、小驼峰字段名 → 下划线 Java 对象字段(→ §6.1)。
- **[D-02] 补充反序列化 Feature**:未知字段处理、null 赋基本类型、单值转数组、空字符串转 null、未知枚举值、根节点解包、大小写不敏感、数组→Bean、Map→Bean、非 public 字段、autoType 等,凡三框架都支持的纳入通用 Feature,安全风险项默认关闭(→ §6.2、§9.1)。

### 1.3 注解需求(A)

- **[A-01] 统一 `@AJSON` 前缀注解体系**:跨三框架一致生效的字段/类型级注解(`@AJSONField` / `@AJSONIgnore` / `@AJSONFormat` / `@AJSONType` / `@AJSONInclude`)(→ §7)。
- **[A-02] 注解可扩展**:新增注解属性默认可选、预留 `@Repeatable` 容器注解,后续 Feature 通过新增属性/注解承载(→ §7.2)。

### 1.4 扩展性需求(E)

- **[E-01] Feature 体系可扩展**:支持后续持续新增 Feature,新增只需「定义枚举项 + 各实现层映射」,不修改已有定义、不破坏调用方(→ §3、§4.1)。
- **[E-02] 三框架支持门槛**:新增 Feature 必须三框架(jackson/gson/fastjson2)都原生支持才可纳入通用 Feature;部分支持的标注降级,不支持的则不纳入(→ §3、§9.1)。
- **[E-03] 向后兼容**:现有 `JSONProvider` / `JSONUtil` 对外 API 不破坏,旧方法保留并委托,新增能力以新方法/新配置载体演进(→ §8.3)。

---

## 2. 背景与目标

### 2.1 背景

`autil-json` 提供了一套「SPI 契约 + 多实现」的 JSON 门面(`JSONProvider` / `JSONUtil`),当前接口按**使用场景**硬编码方法:

- `toJSONString` / `toJSONStringSnake` / `toJSONStringPrettyFormat` / `toJSONStringLarge` / `toJSONStringForStorage`
- `parseObject` / `parseObjectSnake` / `parseArray` / `parseArraySnake`

这种设计的问题:
1. **场景无法自由组合**——例如「下划线 + 紧凑」「驼峰 + 格式化」没有对应方法,每加一种组合就要加一个方法,接口持续膨胀。
2. **`config` 参数名存实亡**——`toJSONString(Object, Map)` / `parseObject(String, Class, Map)` 的 `config` 在各实现中**全链路被丢弃**,是"已声明未实现"的扩展点。
3. **缺少统一注解**——字段级序列化格式(`format`)、字段名映射、忽略等无法跨框架统一配置,只能各自依赖 `@JSONField` / `@JsonProperty` / `@SerializedName`,框架绑定强。

### 2.2 目标

定义一套**框架无关**的 JSON Feature 特性规范:
- **序列化 Feature**:命名风格、输出格式、null 值策略、日期时间格式化等可配置、可组合。
- **反序列化 Feature**:字段映射规则、容错策略、类型转换等可配置。
- **统一注解**:跨 jackson/gson/fastjson 三个实现一致生效的字段级配置。
- **向后兼容**:现有 `JSONProvider` / `JSONUtil` 的对外 API 不破坏,新增能力以新增方法/新配置载体演进。

---

## 3. 设计原则

1. **框架无关**——Feature 定义在 `json-core`,实现层负责映射到各框架原生能力;业务侧只依赖 Feature 抽象,不感知底层框架。
2. **可组合**——Feature 是正交的开关/枚举,可任意组合,不做"每场景一个方法"的硬编码。
3. **默认兼容**——所有 Feature 有明确默认值,默认行为与现有 `JSONProvider` 一致,保证升级无感。
4. **向后兼容**——保留现有方法,新增能力通过新接口/新注解承载,不删除不重签名。
5. **三框架支持门槛**——一个 Feature 只有当 **jackson、gson、fastjson2 三个框架都原生支持**时,才可作为**通用 Feature** 纳入规范;仅单个/少数框架支持的能力,不建议作为通用 Feature,可保留为框架特有扩展或明确标注"部分支持"。
6. **可扩展性**——Feature 体系本身开放:新增 Feature 只需"定义枚举项 + 各实现层映射",不修改已有 Feature 定义、不破坏现有调用方;规范预留 `自定义 Feature` 扩展点,见 §4.1。

---

## 4. Feature 特性规范总览

Feature 分为三大类:

| 类别 | 说明 | 承载方式 |
|---|---|---|
| **序列化 Feature** | 控制"Java 对象 → JSON 文本"的行为 | 枚举 `SerializeFeature` |
| **反序列化 Feature** | 控制"JSON 文本 → Java 对象"的行为 | 枚举 `DeserializeFeature` |
| **字段级注解** | 控制"某个字段/类"的序列化细节 | `@AJSON` 系列注解(见 §7) |

配置载体(三选一,优先级从高到低):
1. **调用级**:`JSONUtil.toJSONString(obj, JsonConfig)` —— 单次调用的临时配置(替代失效的 `Map<String,Object>`)
2. **注解级**:`@AJSON` 系列注解(见 §7)—— 字段/类上的持久配置
3. **实现级**:SPI Provider 自带的默认实现能力(如 fastjson 的 `SupportSmartMatch`)

### 4.1 可扩展性设计(核心)

Feature 体系必须支持**后续持续新增 Feature**,设计上保证扩展成本低、不破坏已有调用。

**新增一个 Feature 的完整流程:**

1. **可行性评估(前置门槛)**:确认该能力**在 jackson / gson / fastjson2 三个框架中是否都原生支持**。
   - 三框架都支持 → 纳入通用 Feature 定义;
   - 仅部分支持 → 不建议纳入通用 Feature;若确有需要,标注"部分支持",并记录不支持框架的差异(见 §9 收敛清单),或作为框架特有扩展保留。
2. **定义枚举项**:在对应枚举(`SerializeFeature` / `DeserializeFeature` / `NamingStyle` 等)中新增枚举值,带明确的 javadoc 与默认值。
3. **JsonConfig 承载**:在 `JsonConfig` 增加对应配置项(或通过已有 `customFeature` 扩展点承载)。
4. **各实现层映射**:在 `json-jackson` / `json-gson` / `json-fastjson` 三个实现中,把新 Feature 映射到对应框架原生 API。
5. **测试与文档**:新增 Feature 专项测试(三框架行为一致性)+ 更新映射对照表(§9)与使用文档。

**扩展性保障机制:**

| 机制 | 说明 |
|---|---|
| **枚举驱动的封闭开关** | Feature 即枚举项,新增 Feature 不修改已有枚举项与已有逻辑,天然向后兼容 |
| **`JsonConfig` 增量扩展** | 新增配置项以新增方法/属性承载,不改变既有 builder 的既有方法 |
| **`@AJSON` 注解预留属性** | 注解设计预留扩展语义,新 Feature 优先复用既有属性,必要时新增属性(默认值不影响已有行为) |
| **自定义 Feature 扩展点** | 预留 `JsonConfig.customFeature`(Map)与 SPI 接口层自定义钩子,供特殊场景在不改核心枚举的前提下扩展(非推荐路径,优先走标准枚举) |
| **不支持的框架降级策略** | 若某实现层不支持某 Feature,默认采用"安全降级"(回退到默认行为),并在日志/文档标注,不抛错中断 |

---

## 5. 序列化 Feature 详细定义

### 5.1 字段命名风格(需求 S-01)

枚举 `NamingStyle`:

| 取值 | 说明 | Jackson 对应 | Gson 对应 | Fastjson2 对应 |
|---|---|---|---|---|
| `LOWER_CAMEL` | 小驼峰 `userName` | `LOWER_CAMEL_CASE` | `IDENTITY`(默认) | `CamelCase`(默认) |
| `UPPER_CAMEL` | 大驼峰 `UserName` | `UPPER_CAMEL_CASE` | `UPPER_CAMEL_CASE` | `PascalCase` |
| `SNAKE_CASE` | 下划线 `user_name` | `SNAKE_CASE` | `LOWER_CASE_WITH_UNDERSCORES` | `SnakeCase` |
| `KEBAB_CASE` | 中划线 `user-name`(补充) | `KEBAB_CASE` | `LOWER_CASE_WITH_DASHES` | `KebabCase` |

> 说明:现有 `toJSONStringSnake` 等价于 `NamingStyle.SNAKE_CASE`,作为兼容保留。

### 5.2 输出格式 / 缩进(需求 S-02)

枚举 `OutputFormat` + 缩进参数:

| 配置 | 说明 |
|---|---|
| `COMPACT` | 紧凑输出:去除不必要的空格与换行(`{"a":1,"b":2}`) |
| `PRETTY` | 格式化输出:带缩进与换行,缩进长度可配置(**2 / 4 / 其他整数值**) |

- 现有 `toJSONStringPrettyFormat` 等价于 `PRETTY + 缩进2`(与 Gson/Jackson 默认 pretty 一致),作为兼容保留。
- **Fastjson2 差异点**:其 `PrettyFormat` 默认缩进为 1 空格,需在实现层显式对齐为统一缩进值。

### 5.3 null 值字段处理(需求 S-03)

枚举 `NullStrategy`:

| 取值 | 说明 | Jackson 对应 | Gson 对应 | Fastjson2 对应 |
|---|---|---|---|---|
| `ALWAYS` | 输出 null 字段 | `JsonInclude.ALWAYS` | `serializeNulls()` | `WriteNulls` |
| `SKIP` | null 字段不序列化 | `JsonInclude.NON_NULL` | 默认(不输出 null) | `SkipNullField` |
| `THROW` | null 字段序列化时抛异常 | —(需自定义) | —(需自定义) | —(需自定义) |

> 说明:`THROW` 是增强语义,需在实现层做统一校验(检测 null 字段则抛 `JsonConfigException`)。现有 `toJSONStringForStorage` 在 Jackson 实现下等价于 `SKIP`,Fastjson2/Gson 下语义不完全一致——这正是 Feature 规范要收敛的差异。

### 5.4 日期时间 / JDK 内置对象自定义格式化(需求 S-04)

- **统一注解**:`@AJSONField(format = "yyyy-MM-dd HH:mm:ss")` 标注在 `LocalDateTime` / `LocalDate` / `LocalTime` / `Date` 等字段上,实现层读取注解的 `format` 并映射到各框架的日期适配器:
  - Jackson:`@JsonFormat(pattern=...)` → 由 SPI 实现的 `JavaTimeModule` 定制
  - Gson:`registerTypeAdapter(LocalDateTime, Iso8601Adapter)` → 按注解格式定制
  - Fastjson2:`@JSONField(format=...)` → 原生支持
- **全局默认格式**:调用级 `JsonConfig.setDateFormat(...)` 提供默认格式兜底。
- **内置对象扩展**:`BigDecimal` / `BigInteger` 的序列化方式(字符串 vs 数字)、`UUID` / `Duration` / `Instant` 等 JDK 内置类型的格式,纳入 Feature `jdkTypeHandling`。

### 5.5 补充序列化 Feature(参考三大框架可配置能力)

| Feature | 取值 / 语义 | 说明 | 来源 |
|---|---|---|---|
| `enumStyle` | `NAME` / `ORDINAL` / `TO_STRING` | 枚举序列化方式 | Jackson `WRITE_ENUMS_USING_TO_STRING`、Fastjson2 |
| `mapKeyOrder` | 是否按键排序 | Map 输出顺序是否排序 | Jackson `ORDER_MAP_ENTRIES_BY_KEYS` |
| `writeClassName` | 是否写入类型信息(多态) | 序列化时写入 `@type` 类型标识 | Fastjson2 `WriteClassName`、Jackson `WRITE_CLASS_NAME` |
| `emptyBeanHandling` | `FAIL` / `WRITE_EMPTY` | 空 Bean 是否抛异常/输出 `{}` | Jackson `FAIL_ON_EMPTY_BEANS` |
| `htmlEscape` | 是否转义 HTML 特殊字符(`<` `>` `&` 等) | 默认转义 | Gson `disableHtmlEscaping()`、Jackson 默认转义 |
| `unicodeEscape` | 是否转义非 ASCII 字符 | 兼容纯 ASCII 场景 | Fastjson2 `BrowserCompatible` |
| `referenceDetection` | 循环引用检测 | 对象环是否检测/忽略/抛异常 | Jackson `SerializationFeature.FAIL_ON_SELF_REFERENCES`、Fastjson2 `ReferenceDetection` |
| `singleValueAsArray` | 单值数组展开(反序列化侧) | 见反序列化补充 | 见下 |
| `fieldOrder` | 字段排序:`DECLARED` / `ALPHABETICAL` / 注解 `ordinal` | 输出字段顺序 | Jackson `SORT_PROPERTIES_ALPHABETICALLY`、Fastjson2 `@JSONType(orders)` |

---

## 6. 反序列化 Feature 详细定义

### 6.1 字段映射规则(需求 D-01)

枚举 `FieldMapping`:

| 取值 | 说明 | 语义 |
|---|---|---|
| `SMART` | 智能映射:驼峰/下划线双向兼容,忽略大小写 | Fastjson2 `SupportSmartMatch`、Jackson `ACCEPT_CASE_INSENSITIVE_PROPERTIES` |
| `EXACT` | 仅同名字段映射 | Gson 默认、Jackson 默认 |
| `SNAKE_TO_CAMEL` | 下划线字段名 → 小驼峰 Java 字段 | Gson `FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES` 反向 |
| `CAMEL_TO_SNAKE` | 小驼峰字段名 → 下划线 Java 字段 | 同上反向 |

> 说明:现有 `parseObjectSnake` 等价于 `SNAKE_TO_CAMEL`,作为兼容保留。

### 6.2 补充反序列化 Feature

| Feature | 取值 / 语义 | 说明 | 来源 |
|---|---|---|---|
| `unknownFieldHandling` | `IGNORE` / `FAIL` | 未知字段是否忽略或抛异常 | Jackson `FAIL_ON_UNKNOWN_PROPERTIES`(默认 fail)、Gson 默认 ignore |
| `nullForPrimitive` | `ALLOW` / `FAIL` | null 赋给基本类型(如 `int`)是否报错 | Jackson `FAIL_ON_NULL_FOR_PRIMITIVES` |
| `singleValueAsArray` | 单值自动转数组 | `[1]` 与 `1` 都能反序列化为 `List` | Jackson `ACCEPT_SINGLE_VALUE_AS_ARRAY` |
| `emptyStringAsNull` | 空字符串视为 null | `""` → null(对象类型) | Jackson `ACCEPT_EMPTY_STRING_AS_NULL_OBJECT` |
| `unknownEnumValue` | `FAIL` / `NULL` / `DEFAULT` | 未知枚举值处理 | Jackson `READ_UNKNOWN_ENUM_VALUES_AS_NULL` / `USING_DEFAULT_VALUE` |
| `rootValueUnwrap` | 是否解包根节点 | 处理 `{"root": {...}}` 包裹 | Jackson `UNWRAP_ROOT_VALUE` |
| `ignoreCase` | 属性名大小写不敏感 | 与 `SMART` 联动 | Jackson `ACCEPT_CASE_INSENSITIVE_PROPERTIES` |
| `arrayToBean` | JSON 数组 → Java Bean | 数组按序映射到字段 | Fastjson2 `SupportArrayToBean` |
| `mapToBean` | Map → Java Bean | 直接支持 Map 反序列化为 Bean | Fastjson2 `SupportMapToBean` |
| `nonPublicField` | 支持非 public 字段反序列化 | 绕过 getter/setter 直接设字段 | Fastjson2 `SupportNonPublicField`、Jackson 需配置 |
| `autoType` | 自动类型支持 | 反序列化多态,需安全考量 | Fastjson2 `SupportAutoType`(有安全风险,建议默认关闭) |

> **安全提示**:`autoType` / `writeClassName` 涉及反序列化漏洞风险,建议默认关闭,仅显式开启。

---

## 7. 统一注解体系(全部采用 `@AJSON` 前缀)

新增字段/类级注解(置于 `json-core` 或 `annotation-api`),跨三个实现一致生效。**所有注解统一使用 `@AJSON` 前缀(JSON 全大写)**,保持命名一致、可辨识,避免散落为 `@AJsonField`/`@AJsonType`/`@AJsonInclude` 等零散前缀。

### 7.1 注解清单

| 注解 | 目标 | 属性 | 说明 |
|---|---|---|---|
| `@AJSONField` | 字段/方法 | `name`(JSON 字段名)、`ignore`、`format`(日期格式)、`ordinal`(顺序)、`naming`(字段命名覆盖) | 字段级核心注解,替代 `@JSONField`/`@JsonProperty`/`@SerializedName` |
| `@AJSONIgnore` | 字段/方法 | — | 序列化与反序列化均忽略(语义同 `ignore=true`),与 `@AJSONField(ignore=true)` 等价 |
| `@AJSONFormat` | 字段/方法 | `pattern`、`timezone`、`locale` | 日期/时间格式化(与 `@AJSONField.format` 等价,可独立使用) |
| `@AJSONType` | 类型 | `naming`、`ignores`、`includes`、`orders` | 类级默认命名与字段过滤 |
| `@AJSONInclude` | 字段/类型 | `nullStrategy`(对应 §5.3) | null 策略的字段/类型级覆盖 |

> 命名约定:所有 `@AJSON*` 注解统一以 `@AJSON` 为前缀(JSON 全大写);后续新增注解(如需要)一律沿用该前缀,例如 `@AJSONConverter`、`@AJSONSerialize` 等。

### 7.2 注解设计要点(可扩展性)

- **注解属性默认可选**:新增属性时提供默认值,不破坏已标注代码。
- **元注解 `@Repeatable` 预留**:若某类需要多组字段规则,预留 `@AJSONFields`(容器注解)支持重复标注。
- **与原生注解关系**:`@AJSON*` 优先,原生注解(`@JsonProperty` 等)兜底(见 §8.2)。

> **目标**:业务代码只需 `@AJSONField`,无需关心底层是 jackson 还是 gson;三个实现层各自把注解映射到原生能力。

---

## 8. 配置入口与优先级

### 8.1 调用级配置对象 `JsonConfig`(替代失效的 `Map<String,Object>`)

```java
JsonConfig config = JsonConfig.builder()
    .naming(NamingStyle.SNAKE_CASE)
    .output(OutputFormat.PRETTY, 4)      // 格式化,缩进 4
    .nullStrategy(NullStrategy.SKIP)
    .dateFormat("yyyy-MM-dd HH:mm:ss")
    .enumStyle(EnumStyle.TO_STRING)
    .build();

JSONUtil.toJSONString(obj, config);       // 新增方法,接受 JsonConfig
JSONUtil.parseObject(json, clazz, config);
```

### 8.2 优先级(高 → 低)

1. 调用级 `JsonConfig`
2. 注解级 `@AJSON` 系列(见 §7)
3. 实现级 SPI 默认能力

### 8.3 向后兼容

- 现有 `Map<String,Object> config` 方法**保留但标记 `@Deprecated`**,内部转译为 `JsonConfig`(从 Map 读取已知 key),避免破坏现有调用方。
- 现有场景方法(`toJSONStringSnake` 等)保留,内部委托到等价 `JsonConfig` 组合。

---

## 9. 跨框架实现差异(收敛清单)

| 差异点 | Jackson | Gson | Fastjson2 | 规范目标 |
|---|---|---|---|---|
| null 默认策略 | 输出 null | 不输出 null | 输出 null | 由 `NullStrategy` 统一,默认 `SKIP` |
| pretty 默认缩进 | 2 | 2 | 1 | 统一为配置值,默认 2 |
| 未知字段默认 | fail | ignore | ignore | 由 `unknownFieldHandling` 统一,默认 `IGNORE` |
| `*Snake` 反序列化 | 真下划线 | 真下划线 | 依赖 `SupportSmartMatch` 弱化 | 由 `FieldMapping` 统一,实现层对齐 |
| 日期格式默认 | `yyyy-MM-dd'T'HH:mm:ss.SSSSSS` | 同上(已对齐) | 库默认 | 由 `dateFormat` 统一 |

### 9.1 Feature 支持度矩阵(三框架支持门槛判定)

每个 Feature 必须明确标注三框架支持度。**只有三框架都原生支持的才可作为通用 Feature**(对应 §3 原则 5);部分支持的按「部分支持」管理,不支持的禁止纳入通用 Feature。

图例:✅ 原生支持 | ⚠️ 部分/需配置 | ✖ 不支持

| Feature | Jackson | Gson | Fastjson2 | 是否通用 Feature |
|---|---|---|---|---|
| 命名风格 `NamingStyle`(LOWER/UPPER/SNAKE/KEBAB) | ✅ | ✅ | ✅ | ✅ 通用 |
| 输出格式 `OutputFormat`(COMPACT/PRETTY+缩进) | ✅ | ✅ | ✅ | ✅ 通用 |
| null 策略 `NullStrategy`(ALWAYS/SKIP/THROW) | ✅ | ✅ | ✅ | ✅ 通用(THROW 为增强语义,实现层统一) |
| 日期格式化 `@AJSONFormat` | ✅ | ✅ | ✅ | ✅ 通用 |
| 枚举 `enumStyle`(NAME/ORDINAL/TO_STRING) | ✅ | ✅ | ✅ | ✅ 通用 |
| Map 键排序 `mapKeyOrder` | ✅ | ✅(需配置) | ✅ | ✅ 通用 |
| 类型信息 `writeClassName` | ✅ | ✖(无原生) | ✅ | ⚠️ 部分支持 |
| 空 Bean `emptyBeanHandling` | ✅ | ⚠️(默认忽略) | ⚠️ | ⚠️ 部分支持 |
| HTML 转义 `htmlEscape` | ✅ | ✅ | ✅ | ✅ 通用 |
| Unicode 转义 `unicodeEscape` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 循环引用 `referenceDetection` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 字段排序 `fieldOrder` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 字段映射 `FieldMapping`(SMART/EXACT/S2C/C2S) | ✅ | ✅ | ✅ | ✅ 通用 |
| 未知字段 `unknownFieldHandling` | ✅ | ✅ | ✅ | ✅ 通用 |
| 基本类型 null `nullForPrimitive` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 单值转数组 `singleValueAsArray` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 空串转 null `emptyStringAsNull` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 未知枚举 `unknownEnumValue` | ✅ | ⚠️ | ✅ | ⚠️ 部分支持 |
| 根节点解包 `rootValueUnwrap` | ✅ | ✖ | ⚠️ | ⚠️ 部分支持 |
| 大小写不敏感 `ignoreCase` | ✅ | ✅ | ✅ | ✅ 通用 |
| 数组→Bean `arrayToBean` | ✖ | ✖ | ✅ | ✖ 不建议通用 |
| Map→Bean `mapToBean` | ⚠️ | ✖ | ✅ | ⚠️ 部分支持 |
| 非 public 字段 `nonPublicField` | ⚠️ | ✖ | ✅ | ⚠️ 部分支持 |
| autoType(安全风险) | ⚠️ | ✖ | ✅ | ⚠️ 部分支持,默认关闭 |

**处理规则**:
- **✅ 通用**:纳入标准 Feature 定义,三个实现层全部落地。
- **⚠️ 部分支持**:可作为 Feature 定义,但必须在文档明确标注缺失框架的降级行为(安全降级,见 §4.1),或推迟纳入。
- **✖ 不支持**:不建议作为通用 Feature,不纳入标准定义。

---

## 10. 实施计划

| 阶段 | 内容 | 依赖 |
|---|---|---|
| **阶段一** | 定义 `SerializeFeature` / `DeserializeFeature` / `NamingStyle` / `OutputFormat` / `NullStrategy` / `FieldMapping` 枚举,`JsonConfig` 配置对象;对每个候选 Feature 做三框架支持度评审(§9.1),确定"通用/部分/不纳入";补齐行为快照测试(见 `Jackson3Migration.md` 阶段一) | 无 |
| **阶段二** | 定义统一注解 `@AJSONField` 等;`json-core` 增加 `JsonConfig` 版方法(旧方法委托兼容) | 阶段一 |
| **阶段三** | 三个实现层逐个落地 Feature 映射(建议先 jackson,因有 `Jackson3Migration` 计划叠加) | 阶段二 |
| **阶段四** | 全项目回归 + 文档(映射对照表、使用示例) | 阶段三 |

> **与 Jackson3Migration 的关系**:`Jackson3Migration` 关注"换底层实现不破坏行为";本提案关注"统一 Feature 抽象"。两者可并行推进,但建议**先落地本提案的 Feature 抽象,再叠加 Jackson3 迁移**,否则 Jackson3 迁移时又要适配两套 Feature 语义。

---

## 11. 风险与应对

| 风险 | 概率 | 影响 | 应对 |
|---|---|---|---|
| 三框架 Feature 语义存在本质差异 | 高 | 中 | 收敛清单(§9)先行对齐;差异点以文档明示 |
| `JsonConfig` 引入后旧 `Map config` 调用方迁移 | 中 | 中 | 旧方法保留并委托,标记 `@Deprecated`,提供迁移指引 |
| `autoType` / `writeClassName` 安全风险 | 中 | 高 | 默认关闭,仅显式开启,文档加安全警告 |
| 统一注解与各框架原生注解冲突 | 低 | 低 | 明确优先级:`@AJSON*` 优先,原生注解兜底 |
| 与 Jackson3Migration 并行时的双份适配 | 中 | 中 | 建议串行(先 Feature 后迁移),见 §10 |

---

## 12. 验收标准

- [ ] Feature 枚举与 `JsonConfig` 定义完备,默认值兼容现有行为
- [ ] 每个 Feature 完成三框架支持度评审(§9.1),通用 Feature 三框架全部落地;部分支持的明确降级策略
- [ ] 三框架实现层对核心 Feature(`naming` / `output` / `nullStrategy` / `dateFormat`)行为一致(同一 `JsonConfig` 输出相同 JSON)
- [ ] 统一注解 `@AJSONField` 在三个实现下均生效
- [ ] 现有 `JSONProvider` / `JSONUtil` 旧方法全部保留,行为不变
- [ ] 行为快照测试 + Feature 专项测试全绿

---

## 13. 待办 / 下一步

1. 评审本提案,确认 Feature 清单是否完整、命名是否合理。
2. 确认 `JsonConfig` 与 `@AJSON*` 注解的归属模块(`json-core` 或 `annotation-api`)。
3. 排期阶段一(Feature 抽象定义 + 行为快照测试)。
