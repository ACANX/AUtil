# JSONSerialization 通用 REST/RPC JSON 序列化与反序列化接口提案

> 状态:待评审
> 作者:ACANX
> 日期:2026-08-21
> 适用范围:`autil-json` 模块群(`json-core` SPI 契约、`json-jackson` / `json-gson` / `json-fastjson` 三实现)
> 关联提案:
> - `Docs/DevProposal/JsonFeatureProposal.md` — Feature 特性规范(定义 `JsonConfig` 等配置载体与 Feature 语义)
> - `Docs/DevProposal/Jackson3Migration.md` — Jackson 2 → 3 平滑迁移(阶段一行为快照测试、阶段三默认实现切换)

---

## 1. 需求清单

> 本提案的所有需求在此分条列出,作为提案的目标与验收依据。**后续新增的需求在此节增量补充**(新增条目编号顺延),保持清单始终是最新完整的需求全集。
> 编号规则:`M` = 方法(Method)需求,`C` = 配置(Config)需求,`S` = 场景(Scenario)需求,`E` = 扩展性(Extension)需求。每条需求可在正文找到对应实现章节。

### 1.1 方法需求(M)

- **[M-01] 定义独立接口 `JSONSerialization`**:新增单独接口承载**序列化 `serialize`** 与**反序列化 `deserialize`** 两个通用方法,作为 REST API / RPC / SDK 客户端场景下 JSON 转换的统一入口(→ §5.1)。
- **[M-02] `JSONProvider` 继承 `JSONSerialization`**:`JSONProvider` 接口继承 `JSONSerialization`,使 SPI 装配的 Provider 天然具备这两个能力,`JSONUtil` 门面统一暴露(→ §5.1)。
- **[M-03] `serialize` 默认「下划线」**:序列化输出的字段名为**下划线风格**(对应 `NamingStyle.SNAKE_CASE`),匹配 REST/RPC 传输层常见约定(→ §5.1、§5.2)。
- **[M-04] `deserialize` 默认「小驼峰」**:反序列化将下划线 JSON 字段映射回**小驼峰 Java 字段**(对应 `FieldMapping.SNAKE_TO_CAMEL`),与 Java Bean 编码风格一致(→ §5.1、§5.3)。
- **[M-05] 方法必须支持配置参数**:两个方法均需接受配置参数,调用方可按场景传入不同配置覆盖默认值(→ §5.4)。
- **[M-06] 反序列化需支持泛型与集合目标类型**:`deserialize` 需支持 `Class` / `Type`(泛型)目标,满足 RPC 出参、集合响应的反序列化需求(→ §5.3)。

### 1.2 配置需求(C)

- **[C-01] 默认值需与场景吻合**:默认行为面向「传输层约定」,无需额外配置即可覆盖绝大多数 REST/RPC/SDK 调用(→ §5.2)。
- **[C-02] 配置参数类型为 `JsonConfig`**:采用 `JsonFeatureProposal.md` §8.1 定义的 `JsonConfig` 配置对象,替代失效的 `Map<String,Object>`(→ §5.4、§6.1)。
- **[C-03] 配置可覆盖序列化输出细节**:调用方可覆盖 null 策略、日期格式、输出格式(紧凑/缩进)等(→ §5.4、§6.2)。
- **[C-04] 配置可覆盖反序列化容错细节**:调用方可覆盖未知字段处理、未知枚举、基本类型 null 等容错策略(→ §5.4、§6.3)。
- **[C-05] 无配置时零负担**:未传配置时按默认值直接执行,不强制调用方构造 `JsonConfig`(→ §5.4、§5.5)。

### 1.3 场景需求(S)

- **[S-01] HTTP REST API 请求参数序列化**:客户端发起 REST 请求时,把参数对象序列化为下划线 JSON 请求体 / URL 查询串(→ §7.1)。
- **[S-02] HTTP REST API 响应体反序列化**:客户端把下划线 JSON 响应体反序列化为小驼峰 Java Bean(→ §7.2)。
- **[S-03] RPC 接口入参/出参转换**:RPC 调用方与提供方之间的参数/返回值 JSON 转换(→ §7.3)。
- **[S-04] SDK 客户端 HTTP 请求/响应**:SDK 内部统一的 JSON 转换入口,行为可被 SDK 使用方覆盖(→ §7.4)。
- **[S-05] 配置差异隔离**:不同场景(如 A 接口要求紧凑输出、B 接口要求特定日期格式)通过传入不同 `JsonConfig` 隔离,互不影响(→ §5.4)。

### 1.4 扩展性需求(E)

- **[E-01] 与 Feature 规范联动**:新增能力语义与 `JsonFeatureProposal.md` 的 Feature 体系完全一致,后续新增 Feature 无需改动本接口签名(→ §6)。
- **[E-02] 与 Jackson3 迁移兼容**:接口契约不绑定具体实现,`Jackson3Migration` 阶段切换默认实现时本接口签名零变化(→ §6、§8)。
- **[E-03] 向后兼容**:现有 `JSONProvider` / `JSONUtil` 方法全部保留,新增接口为纯增量(→ §8.1)。

---

## 2. 背景与目标

### 2.1 背景

`autil-json` 现有 `JSONProvider` 接口按**使用场景**硬编码方法:

- 序列化:`toJSONString` / `toJSONStringSnake` / `toJSONStringPrettyFormat` / `toJSONStringLarge` / `toJSONStringForStorage`
- 反序列化:`parseObject` / `parseObjectSnake` / `parseArray` / `parseArraySnake`

面向 **HTTP REST / RPC / SDK 客户端** 场景时,现状存在明显不便:

1. **无面向「传输层」的默认语义**——REST 参数要下划线、响应要小驼峰,现有方法要么默认驼峰(`toJSONString`),要么硬编码下划线(`toJSONStringSnake`),无法表达"序列化下划线 + 反序列化小驼峰"这一对对称的传输层约定。
2. **`config` 参数名存实亡**——`toJSONString(Object, Map)` / `parseObject(String, Class, Map)` 的 `config` 在各实现中**全链路被丢弃**,是"已声明未实现"的扩展点(详见 `JsonFeatureProposal.md` §2.1)。
3. **场景差异无法按调用覆盖**——不同接口对 null 策略、日期格式、未知字段容忍度不同,现有方法无法按调用传入配置。
4. **泛型/集合目标支持不足**——RPC 出参、集合响应需要按 `Type` 反序列化,现有入口不统一。
5. **职责未独立**——通用序列化语义混在 `JSONProvider` 接口中,不利于单独复用与演进;抽为独立接口 `JSONSerialization` 后,语义清晰、可独立测试、可被 `JSONProvider` 之外的实现复用。

`JsonFeatureProposal.md` 已定义 `JsonConfig` 配置对象与 Feature 语义,本提案是其在「传输层通用接口」上的落地面。

### 2.2 目标

定义**独立接口 `JSONSerialization`**,提供两个通用方法,形成面向 REST/RPC/SDK 的统一 JSON 转换入口:

- **`serialize`**:默认**下划线**输出,可按场景传入 `JsonConfig` 覆盖输出细节。
- **`deserialize`**:默认**下划线→小驼峰**映射,可按场景传入 `JsonConfig` 覆盖容错与映射细节。

`JSONProvider` 继承 `JSONSerialization`,SPI 装配的 Provider 天然具备该能力。

一句话概括:**"序列化按下划线、反序列化回小驼峰,细节可配置"** —— 覆盖 REST 参数、响应体、RPC 入参/出参、SDK 请求/响应四个场景。

---

## 3. 设计原则

1. **默认即用**——不传配置即可覆盖绝大多数传输层场景,配置用于覆盖少数差异。
2. **配置驱动**——所有可变行为通过 `JsonConfig`(见 `JsonFeatureProposal.md` §8.1)表达,不新增按场景硬编码的方法。
3. **框架无关**——接口定义在 `json-core`,三个实现层各自映射到底层框架(见 `JsonFeatureProposal.md` §3)。
4. **接口职责独立**——通用序列化/反序列化抽为 `JSONSerialization` 接口,`JSONProvider` 以继承方式获得该能力;接口可独立复用、独立演进。
5. **向后兼容**——新增接口与继承关系不修改、不删除、不重签名任何现有方法(见 §8.1)。
6. **与两份关联提案一致**——Feature 语义遵循 `JsonFeatureProposal.md`;实现不绑定具体框架,兼容 `Jackson3Migration` 的默认实现切换(见 §6、§8.3)。

---

## 4. 接口与现状对比

| 维度 | 现有场景方法 | 本提案新增接口 |
|---|---|---|
| 载体 | `JSONProvider` 上的场景方法 | 独立 `JSONSerialization` 接口(`JSONProvider` 继承) |
| 目标 | 单一场景硬编码(驼峰 / Snake / Pretty / Storage) | 传输层通用(序列化下划线、反序列化小驼峰) |
| 配置 | `Map<String,Object>` 全链路丢弃 | `JsonConfig` 全链路生效 |
| 默认命名 | 随方法名定死 | 下划线(序列化) / 下划线→小驼峰(反序列化) |
| 泛型支持 | 部分方法支持 `Type` | 统一支持 `Class` / `Type` |
| 与 Feature 体系 | 无关联 | 完全对齐(§6) |

---

## 5. 接口与配置设计

### 5.1 接口定义与方法签名

定义独立接口 `JSONSerialization`(对应需求 M-01、M-02):

```java
/**
 * 通用 JSON 序列化/反序列化契约:面向 HTTP REST / RPC / SDK 客户端的传输层场景。
 * 默认语义:序列化输出下划线字段;反序列化将下划线 JSON 字段映射为小驼峰 Java 字段。
 * 两个方法均接受 {@link JsonConfig} 配置参数,可按场景覆盖默认行为。
 */
@Alpha
public interface JSONSerialization {

    /**
     * 通用序列化:适用于 HTTP REST 请求参数、RPC 入参、SDK 请求体。
     * 默认:字段名下划线、紧凑输出、null 字段不输出。
     * 可通过 config 覆盖命名风格、null 策略、日期格式、输出格式等。
     *
     * @param object Java 对象
     * @param config 序列化配置,可为 null(按默认值执行)
     * @return JSON 字符串
     */
    @Alpha
    String serialize(Object object, JsonConfig config);

    /**
     * 通用反序列化:适用于 HTTP REST 响应体、RPC 出参、SDK 响应体。
     * 默认:下划线 JSON 字段 → 小驼峰 Java 字段。
     * 可通过 config 覆盖字段映射、未知字段、未知枚举等容错策略。
     *
     * @param jsonStr    JSON 字符串
     * @param targetType 目标类型(Class 或 Type,支持泛型)
     * @param config     反序列化配置,可为 null(按默认值执行)
     * @param <T>        目标类型参数
     * @return 反序列化结果
     */
    @Alpha
    <T> T deserialize(String jsonStr, Type targetType, JsonConfig config);
}
```

`JSONProvider` 继承 `JSONSerialization`,获得该能力(现有方法全部保留):

```java
@Alpha
public interface JSONProvider extends JSONSerialization {
    // ... 现有 toJSONString* / parseObject* / parseArray* 方法全部保留,不再赘述
}
```

说明:

- `deserialize` 接收 `Type` 而非仅 `Class<T>`,`Class` 是 `Type` 的特例,天然覆盖 M-06 的泛型/集合需求(集合响应传 `TypeReference` / `ParameterizedType`)。
- 若未来需要"下划线数组反序列化",可在 `JSONSerialization` 上后续扩展 `deserializeArray`;本提案聚焦最通用的两个方法(见 §10 待办)。
- `JSONSerialization` 独立于 `JSONProvider`,任何具备序列化能力的实现(不限于 SPI Provider)都可复用该接口。

`JSONUtil` 门面暴露静态方法,内部委托给 `PROVIDER`(Provider 实现了 `JSONSerialization`):

```java
public static String serialize(Object object) { ... }                    // 无配置,默认
public static String serialize(Object object, JsonConfig config) { ... }
public static <T> T deserialize(String json, Class<T> clazz) { ... }
public static <T> T deserialize(String json, Type type, JsonConfig config) { ... }
```

> **为何新增独立接口而非改造**:`serialize` 与 `toJSONStringSnake` 都默认下划线,但前者**接受 `JsonConfig` 且为传输层专用语义**,职责不同;抽为 `JSONSerialization` 接口使通用序列化能力与场景化方法解耦,改造旧方法会破坏现有调用方,违背 §3 原则 5(见 §8.1)。

### 5.2 序列化默认值(对应需求 M-03)

| 维度 | 默认值 | 理由 |
|---|---|---|
| 命名风格 | `NamingStyle.SNAKE_CASE` | REST/RPC 传输层通用约定(下划线) |
| 输出格式 | `OutputFormat.COMPACT` | 请求体/响应体通常紧凑传输 |
| null 字段 | `NullStrategy.SKIP` | 请求参数通常不携带 null,省流量 |
| 日期格式 | `JsonConfig` 未配置时的全局默认 | 依赖 `JsonFeatureProposal.md` §5.4 的统一格式 |
| 枚举 | `EnumStyle.NAME` | 传输层枚举默认用名称 |

### 5.3 反序列化默认值(对应需求 M-04)

| 维度 | 默认值 | 理由 |
|---|---|---|
| 字段映射 | `FieldMapping.SNAKE_TO_CAMEL` | 下划线 JSON → 小驼峰 Java Bean |
| 未知字段 | `unknownFieldHandling.IGNORE` | 响应体新增字段不应破坏调用方(服务端向后兼容) |
| 未知枚举 | `unknownEnumValue.NULL`(或 `DEFAULT`) | 容错,不因枚举演进抛错 |
| 基本类型 null | 默认(非 FAIL) | 容错 |

> **与 `parseObjectSnake` 的关系**:`parseObjectSnake(json, Class)` 等价于「`FieldMapping.SNAKE_TO_CAMEL` + 无其他配置」的简写;`deserialize` 的 `JsonConfig` 版本是其**可配置超集**,旧方法保留兼容(见 §8.1)。

### 5.4 配置参数:按场景覆盖(对应需求 M-05、C-02、C-05)

- 方法签名中 `JsonConfig config` 可为 `null`:`null` 时按 §5.2 / §5.3 默认值执行,**调用方零负担**。
- 传入 `JsonConfig` 时,其设置**逐项覆盖**默认值;未设置的项仍用默认值(合并语义)。
- 不同场景传入不同 `JsonConfig`,互不影响(对应需求 S-05):

```java
// 场景 A:默认(下划线、紧凑、null 不输出)
JSONUtil.serialize(user);

// 场景 B:接口要求 null 也输出(便于对方兜底)
JsonConfig cfgB = JsonConfig.builder().nullStrategy(NullStrategy.ALWAYS).build();
JSONUtil.serialize(user, cfgB);

// 场景 C:接口要求特定日期格式 + 缩进
JsonConfig cfgC = JsonConfig.builder()
        .dateFormat("yyyy-MM-dd HH:mm:ss")
        .output(OutputFormat.PRETTY, 2)
        .build();
JSONUtil.serialize(user, cfgC);

// 响应反序列化:默认下划线→小驼峰
User user = JSONUtil.deserialize(respJson, User.class);

// 响应反序列化:覆盖为仅同名字段严格映射
JsonConfig strict = JsonConfig.builder().fieldMapping(FieldMapping.EXACT).build();
User user2 = JSONUtil.deserialize(respJson, User.class, strict);
```

### 5.5 方法命名

- **序列化 `serialize`**:通用、简洁,与"序列化"语义一一对应,不绑定 JSON 字符串或具体传输形态。
- **反序列化 `deserialize`**:与 `serialize` 对称,接收 `Type` 目标,天然覆盖泛型。
- 现有 `toJSONString*` / `parseObject*` 系列保留不动,与 `serialize` / `deserialize` 并存;后者是其"可配置超集"(见 §8.1)。

---

## 6. 与关联提案的衔接

### 6.1 与 `JsonFeatureProposal.md` 的关系

| 本提案 | 依赖的 Feature 提案内容 |
|---|---|
| `JsonConfig` 配置对象 | `JsonFeatureProposal.md` §8.1 定义、§8.3 向后兼容 |
| `NamingStyle` / `OutputFormat` / `NullStrategy` / `FieldMapping` | `JsonFeatureProposal.md` §5.1 / §5.2 / §5.3 / §6.1 定义 |
| 日期格式、枚举、未知字段等容错 Feature | `JsonFeatureProposal.md` §5.4 / §5.5 / §6.2 定义 |
| Feature 支持度矩阵(三框架门槛) | `JsonFeatureProposal.md` §9.1 判定哪些 Feature 可作为通用配置 |

**依赖关系**:本提案的配置项**全部复用** Feature 提案的枚举与 `JsonConfig`,不新增平行概念;Feature 提案新增 Feature 时,`JSONSerialization` 无需改签名(对应 E-01)。

**顺序建议**:先落地 `JsonFeatureProposal.md` 的 `JsonConfig` 与核心枚举(其阶段一),本提案即可在此之上实施。

### 6.2 与 `Jackson3Migration.md` 的关系

- `Jackson3Migration` 关注"换底层实现不破坏行为";本提案接口定义在 `json-core` 的 SPI 契约上,**不引用任何具体框架 API**。
- 本提案方法在三个实现层映射时,只依赖各自框架的**原生能力**(命名策略、include、日期适配器),与 `Jackson3Migration` 阶段一的行为快照测试**相互增强**:`serialize` / `deserialize` 的默认行为也应纳入快照,升级 jackson3 后同一断言必须全绿。
- `Jackson3Migration` 阶段三切换默认实现时,`JSONSerialization` 接口签名零变化,行为由快照测试兜底(对应 E-02)。

### 6.3 依赖顺序

```
JsonFeatureProposal(JsonConfig + 枚举)  →  本提案(JSONSerialization 接口 + 默认语义)  →  Jackson3Migration(换实现)
```

三者可并行评审,但**实施上有先后**:本提案依赖 Feature 提案的 `JsonConfig`;若 Feature 提案延后,本提案可先以内部等价配置对象占位,后续再收敛(见 §10)。

---

## 7. 场景映射

以下四个场景均可由 `JSONSerialization` 两个方法覆盖;**默认配置即可满足绝大多数情况**,差异通过传入 `JsonConfig` 覆盖。

### 7.1 HTTP REST API 请求参数序列化(S-01)

```java
// 默认:下划线字段、紧凑输出、null 跳过
String body = JSONUtil.serialize(requestParams);
// 请求头: Content-Type: application/json  请求体: {"user_name":"ACANX","page":1}
```

- 覆盖场景:查询参数对象、POST/PUT 请求体、表单对象等。
- 差异覆盖:若某接口要求 null 也输出,传 `nullStrategy(ALWAYS)`。

### 7.2 HTTP REST API 响应体反序列化(S-02)

```java
// 默认:下划线 JSON → 小驼峰 Java 字段,忽略响应中新增的未知字段
ApiResponse resp = JSONUtil.deserialize(respBody, ApiResponse.class);
```

- 覆盖场景:统一响应包裹、分页结果、错误码对象等。
- 差异覆盖:若需严格校验响应结构,传 `fieldMapping(EXACT)` + `unknownFieldHandling(FAIL)`。

### 7.3 RPC 接口入参/出参转换(S-03)

```java
// 入参序列化(下划线)+ 出参反序列化(下划线→小驼峰)
String requestJson = JSONUtil.serialize(rpcRequest);
RpcResult<List<Order>> result = JSONUtil.deserialize(
        respJson, new TypeReference<List<Order>>() {}.getType());
```

- `Type` 目标支持泛型集合 / 嵌套泛型出参。

### 7.4 SDK 客户端 HTTP 请求/响应(S-04)

- SDK 内部统一使用 `serialize` / `deserialize` 作为 JSON 转换入口,默认调用即可;同时把 `JsonConfig` 作为可覆盖点暴露给使用方:

```java
public class ApiClient {
    public <T> T execute(Request req, Class<T> respClass) {
        String body = JSONUtil.serialize(req, this.globalConfig);
        String respBody = http.post(uri, body);
        return JSONUtil.deserialize(respBody, respClass, this.globalConfig);
    }
    // 使用方可通过 setGlobalConfig(JsonConfig) 覆盖默认传输行为
}
```

---

## 8. 向后兼容与风险

### 8.1 向后兼容

- **零破坏**:新增 `JSONSerialization` 接口;`JSONProvider` 仅**增加继承关系**,不修改、不删除、不重签名现有任何方法。
- **实现层**:现有三个 Provider 实现类因 `JSONProvider extends JSONSerialization` 而需补实现 `serialize` / `deserialize`;`JSONSerialization` 提供 `default` 实现(委托到现有方法 + 忽略 `JsonConfig`)兜底,保证编译不破;完整支持由后续 Feature 阶段补齐。
- **旧方法保留**:`toJSONStringSnake` / `parseObjectSnake` 等继续可用,与 `serialize` / `deserialize` 并存;后者是其"可配置超集",旧方法内部可委托到等价 `JsonConfig` 组合(与 `JsonFeatureProposal.md` §8.3 一致)。

### 8.2 风险登记

| 风险 | 概率 | 影响 | 应对 |
|---|---|---|---|
| `JsonConfig` 未落地导致依赖缺失 | 中 | 高 | 与 Feature 提案阶段一并实施;本提案作为其第一落地场景 |
| 三实现层对「下划线」「未知字段」语义不一致 | 中 | 中 | 复用 Feature 提案 §9 收敛清单与行为快照测试 |
| 默认「忽略未知字段」可能掩盖响应结构错误 | 低 | 低 | 文档明示;需要严格场景传 `EXACT` / `FAIL` 配置 |
| `serialize` / `deserialize` 与 `toJSONString*` / `parseObject*` 语义重叠引发困惑 | 低 | 低 | §5.5 命名与文档说明职责边界 |

### 8.3 与 Jackson3 迁移叠加

- 本接口方法在 `json-jackson`(Jackson 2)实现落地后,`json-jackson3` 需通过同一套快照断言(见 `Jackson3Migration.md` 阶段一/阶段二)。
- `Jackson3Migration` 阶段三的显式优先级表已保证 `jackson3 > jackson2 > gson > fastjson2`,`serialize` / `deserialize` 的行为一致性由快照兜底。

---

## 9. 验收标准

- [ ] `JSONSerialization` 接口定义 `serialize` / `deserialize` 两个方法,`JSONProvider` 继承之
- [ ] `JSONUtil` 暴露 `serialize` / `deserialize` 静态方法
- [ ] 不传配置时:`serialize` 输出下划线、紧凑、null 跳过;`deserialize` 下划线→小驼峰、忽略未知字段(行为快照测试固化)
- [ ] 传 `JsonConfig` 时:命名 / null / 日期 / 输出格式 / 字段映射等**逐项可覆盖**,未配置项保持默认
- [ ] `Class` 与 `Type`(泛型 / 集合)目标反序列化均可用
- [ ] 三个实现层(含后续 jackson3)行为一致;现有方法全部保留,行为不变
- [ ] 与 `JsonFeatureProposal.md` / `Jackson3Migration.md` 的依赖关系、实施顺序在文档中明确

---

## 10. 待办 / 下一步

1. 评审本提案,确认接口命名(`JSONSerialization`)、方法命名(`serialize` / `deserialize`)与配置语义。
2. 与 `JsonFeatureProposal.md` 评审合并推进:其阶段一(`JsonConfig` + 枚举)是本提案的前置。
3. 排期:Feature 提案阶段一 → 本提案接口落地 + 行为快照 → 三实现层映射 → 纳入 `Jackson3Migration` 快照回归。
