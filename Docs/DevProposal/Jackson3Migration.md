# Jackson 2 → Jackson 3 平滑升级方案

> 状态:待评审
> 作者:ACANX
> 日期:2026-08-21
> 适用范围:`autil-json` 模块群(重点为 `json-jackson`)
> 关联提交:`a90ae38 refactor(json): 调整SPI加载优先级为jackson>gson>fastjson2`

---

## 1. 背景与目标

### 1.1 现状

`autil-json/json-jackson` 模块当前基于 **Jackson 2**(`com.fasterxml.jackson.core:jackson-databind:2.22.2` + `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.2`),通过 JDK `ServiceLoader` SPI 作为 `JSONProvider` 的一个实现被 `json-core` 装配。

**该模块是基础组件,被大量第三方模块及下游项目依赖。** 升级必须保证:
- 升级后不出现不兼容报错;
- 编译通过;
- 对现有使用方行为无感。

### 1.2 目标

将 Jackson 实现从 **Jackson 2 平滑迁移到 Jackson 3**,过程中保持对外 API 与行为稳定,支持随时回滚。

---

## 2. 现状盘点(已核实)

### 2.1 Jackson 使用面(全部收敛在 `json-jackson` 模块)

`autil-json/json-jackson/src/main/java` 下共 3 个源码文件 + 1 个 SPI 注册文件:

| 文件 | 职责 |
|---|---|
| `impl/JacksonProvider.java` | SPI 适配器,实现 `JSONProvider` |
| `JacksonUtil.java` | Jackson 静态包装工具 |
| `JacksonFileUtil.java` | 文件级便利类 |
| `META-INF/services/com.acanx.util.json.JSONProvider` | SPI 注册 |

**Jackson 相关 import 全部集中在上述 3 个类**,约 10 个 API:
`ObjectMapper`、`PropertyNamingStrategies`、`SerializationFeature`、`DeserializationFeature`、`JavaType`、`CollectionType`、`TypeFactory`、`JsonInclude`、`JsonProcessingException`、`TypeReference`、`JavaTimeModule`、`LocalDateTimeSerializer/Deserializer`、`MapperFeature`。

### 2.2 架构缓冲(有利条件)

- `json-core` 的 `JSONProvider` 接口对 Jackson **零依赖**——依赖倒置已成立,升级仅限 `json-jackson` 模块内部。
- 使用方只面对 `JSONUtil` 门面 + SPI 契约,不直接触碰 Jackson API。

### 2.3 风险点

- 现有 `autil-json` 各模块测试**几乎全为空壳**(空方法体/断言被注释),**没有可用的回归测试网**。
- `getPriority` 按**类名子串匹配**(`contains("jackson")`)仲裁优先级,Jackson 2 与未来的 Jackson 3 Provider 会**同分**,排序不稳定。
- Jackson 3 包名从 `com.fasterxml.jackson.*` 变更为 `tools.jackson.*`,属破坏性变更(但使用面窄,可控)。

---

## 3. 升级原则

1. **一次只加新实现,不删旧实现**——避免任何时刻出现"无可用实现"。
2. **默认实现切换可回滚**——通过依赖/优先级一行回退。
3. **以测试固化行为契约**——升级前后同一套断言必须全绿。
4. **外部 API 零变化**——`JSONProvider`、`JSONUtil`、`MimeConst` 等对外契约不动。

---

## 4. 分阶段实施计划

### 阶段一:行为快照基线(当前应立刻做)

把 Jackson 2 的当前行为固化为自动化测试,作为升级回归锚点。

- **为 `JacksonUtil` / `JacksonProvider` / `JacksonFileUtil` 的每个 public 方法编写快照测试**,覆盖:
  - 序列化/反序列化(驼峰、Snake、PrettyFormat、Large、ForStorage)
  - 日期时间格式(`yyyy-MM-dd'T'HH:mm:ss.SSSSSS`)
  - 数组/集合/泛型解析
  - 文件读写
- 断言使用 JUnit 5,替换现有空壳测试。
- **交付物**:`json-jackson` 行为快照测试套件,全绿。

### 阶段二:新增 `json-jackson3` 模块(不修改 `json-jackson`)

- 新建 `autil-json/json-jackson3`,依赖 **Jackson 3**(`tools.jackson.core:*`),实现**同一个** `JSONProvider` SPI,注册同接口 SPI 文件。
- `json-jackson`(Jackson 2)模块**保持不动**,现有下游零影响。
- 模块内部自测:Jackson 3 环境下跑阶段一的快照断言,逐步消除行为差异。
- **交付物**:可独立构建、自测通过的新模块。

### 阶段三:切换默认实现(可回滚)

- 将 `getPriority` 由"类名子串匹配"改为**显式优先级表**:

```java
private static final Map<String, Integer> PRIORITIES = Map.of(
    "com.acanx.util.json.impl.Jackson3Provider", 4,
    "com.acanx.util.json.impl.JacksonProvider",  3,
    "com.acanx.util.json.impl.GsonProvider",     2,
    "com.acanx.util.json.impl.FastJSONProvider", 1
);
```

  使 `jackson3 > jackson2 > gson > fastjson2`,顺序确定、可预测。
- 全项目(含 `autil-core` / `autil-test` / `autil-incubator`)跑一遍测试与构建。
- 观察期:默认实现为 jackson3;若下游报兼容问题,**一行依赖/优先级即可回退到 jackson2**。
- **交付物**:默认实现切换 + 全项目回归通过 + 回滚开关文档。

### 阶段四:清理与归档

- 稳定运行一段观察期后,删除 `json-jackson`(Jackson 2)模块及相关依赖与 SPI。
- 在 CHANGELOG / 文档记录迁移说明。

---

## 5. 关键设计决策

### 5.1 为什么新增独立模块而不是就地替换

就地替换会让 `json-jackson` 在迁移窗口内**同时存在两种依赖形态**,编译与运行风险高、回滚困难。独立 `json-jackson3` 模块并行共存,旧模块天然是回滚锚点。

### 5.2 优先级仲裁的显式化

现状 `getPriority` 用类名子串匹配,Jackson 2/3 的 Provider 类名都含 `"jackson"`,会同分导致排序不稳定。改为显式优先级表后,顺序确定,且新实现默认最高。

### 5.3 测试先行是升级的安全网

当前测试空壳,升级时无自动回归能力。阶段一的行为快照测试是**整个升级可信的前提**。

---

## 6. Jackson 3 迁移要点

| 维度 | Jackson 2 | Jackson 3 |
|---|---|---|
| 包名 | `com.fasterxml.jackson.*` | `tools.jackson.*` |
| 核心模块 | `jackson-databind` + `jackson-datatype-jsr310` | `tools.jackson.core:jackson-databind`(jsr310 并入或独立) |
| `ObjectMapper` | 直接构造 | 构建方式/默认值有差异 |
| 版本要求 | 兼容 Java 8+ | 要求 Java 17+ |

本项目 `java.version=21`,满足 Jackson 3 的 Java 要求。迁移差异由阶段一快照测试兜底。

---

## 7. 风险登记与应对

| 风险 | 概率 | 影响 | 应对 |
|---|---|---|---|
| Jackson 3 行为差异(日期/命名/null 策略) | 中 | 中 | 阶段一快照测试锁定,逐步对齐 |
| 优先级仲裁不稳定(同名 Provider 同分) | 高 | 高 | 阶段三显式优先级表 |
| 下游项目升级后报错 | 中 | 高 | 默认实现可回滚;观察期 |
| 无回归测试网 | 高 | 高 | 阶段一补行为快照测试 |
| Jackson 3 依赖引入后版本冲突 | 低 | 中 | 独立模块隔离 + 版本属性集中管理 |

---

## 8. 验收标准

- [ ] 阶段一:行为快照测试全绿(Jackson 2 基线)
- [ ] 阶段二:`json-jackson3` 独立构建、自测通过
- [ ] 阶段三:默认实现为 jackson3,全项目构建与测试通过;回滚开关验证有效
- [ ] 阶段四:旧模块删除后全绿

---

## 9. 待办 / 下一步

1. 评审本方案,确认阶段划分与模块命名(`json-jackson3`)。
2. 排期阶段一(行为快照测试),这是后续一切的前提。
