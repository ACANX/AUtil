# Jackson 2 → Jackson 3 平滑升级方案(同模块双 Provider + 开关仲裁)

> 状态:待评审
> 作者:ACANX
> 日期:2026-08-21
> 适用范围:`autil-json` 模块群(重点为 `json-jackson`)
> 关联提交:`a90ae38 refactor(json): 调整SPI加载优先级为jackson>gson>fastjson2`
> 关联提案:
> - `Docs/DevProposal/JsonFeatureProposal.md` — Feature 特性规范(定义 `JSONConfig` 等配置载体与 Feature 语义)
> - `Docs/DevProposal/HttpApiJsonProposal.md` — `JSONSerialization` 通用 REST/RPC 序列化接口(Provider 需实现 `serialize` / `deserialize`)

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

**本方案的约束与方向**:
- **不新增 maven 模块**——`JacksonProvider`(Jackson 2)与 `Jackson3Provider`(Jackson 3)**共存于 `json-jackson` 同一模块**;
- 通过**三态开关 + 显式优先级表**仲裁两个 Provider 的启用,迁移期可用「显式启用 / 自动自适应」两种方式切换;
- 切换与回滚各只需一行配置,观察期结束才清理旧实现。

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
- **Jackson 2 与 Jackson 3 包名完全隔离**:`com.fasterxml.jackson.*` vs `tools.jackson.*`,且两者 groupId 不同,属于**不同的 maven artifact**——这意味着两套依赖**可以同时存在于同一模块的 classpath,不会发生类冲突**。这是"同模块双 Provider"方案成立的前提。

### 2.3 风险点

- 现有 `autil-json` 各模块测试**几乎全为空壳**(空方法体/断言被注释),**没有可用的回归测试网**。
- `getPriority` 按**类名子串匹配**(`contains("jackson")`)仲裁优先级,`JacksonProvider` 与未来的 `Jackson3Provider` 类名都含 `"jackson"`,会**同分**,排序不稳定——必须在引入双 Provider 的同时改为**显式优先级表**。
- Jackson 3 包名从 `com.fasterxml.jackson.*` 变更为 `tools.jackson.*`,属破坏性变更(但使用面窄,可控)。
- 同模块双依赖会带来**依赖体积与传递面**问题,需用 `optional` 依赖隔离(见 §5.3)。

---

## 3. 升级原则

1. **一次只加新实现,不删旧实现**——避免任何时刻出现"无可用实现"。
2. **不新增 maven 模块**——两个 Provider 在 `json-jackson` 模块内共存,依赖通过 `optional` 控制传递。
3. **默认实现切换可回滚**——通过三态开关 / 依赖 scope 一行回退。
4. **显式优先、自适应兜底**——优先尊重显式开关;未显式指定时,自动探测 classpath 中可用的 Jackson 版本。
5. **以测试固化行为契约**——升级前后同一套断言必须全绿。
6. **外部 API 零变化**——`JSONProvider`、`JSONUtil`、`JSONSerialization`、`MimeConst` 等对外契约不动。

---

## 4. 分阶段实施计划

### 阶段一:行为快照基线(当前应立刻做)

把 Jackson 2 的当前行为固化为自动化测试,作为升级回归锚点。

- **为 `JacksonUtil` / `JacksonProvider` / `JacksonFileUtil` 的每个 public 方法编写快照测试**,覆盖:
  - 序列化/反序列化(驼峰、Snake、PrettyFormat、Large、ForStorage)
  - 日期时间格式(`yyyy-MM-dd'T'HH:mm:ss.SSSSSS`)
  - 数组/集合/泛型解析
  - 文件读写
  - `JSONSerialization.serialize` / `deserialize` 默认语义(见 `HttpApiJsonProposal.md` §5.2/§5.3)
- 断言使用 JUnit 5,替换现有空壳测试。
- **交付物**:`json-jackson` 行为快照测试套件,全绿。

### 阶段二:模块内新增 `Jackson3Provider`(不新增模块,双 Provider 共存)

在 **`json-jackson` 现有模块内部**新增 Jackson 3 实现,与 Jackson 2 实现并存:

1. **pom 依赖**:新增 Jackson 3 依赖,标记 `optional`(不传递给下游):

   ```xml
   <!-- Jackson 3:迁移期新增,optional 保证默认不传递给下游 -->
   <dependency>
       <groupId>tools.jackson.core</groupId>
       <artifactId>jackson-databind</artifactId>
       <version>${jackson3.version}</version>
       <optional>true</optional>
   </dependency>
   ```

   - Jackson 2 依赖**保持 compile 不变**(现有下游靠它获得实现)。
   - `optional` 对本模块自身的编译与测试仍然可见,只是不传递——所以阶段一快照测试可以就地针对两个 Provider 各跑一遍。
   - 若 Jackson 3 传递引入 `com.fasterxml.*` 依赖(如 annotations),需 `<exclusions>` 排除以避免与 Jackson 2 冲突(见 §8 风险)。

2. **新增 `Jackson3Provider`**:基于 `tools.jackson.*` 实现 `JSONProvider`(含 `JSONSerialization.serialize` / `deserialize`)。**独立实现,不 `extends JacksonProvider`**(底层 API 完全不同)。类名与 `JacksonProvider` 区分,避免 SPI 排序同分。

3. **SPI 注册**:`META-INF/services/com.acanx.util.json.JSONProvider` 同时列出两个 Provider:

   ```
   com.acanx.util.json.impl.JacksonProvider
   com.acanx.util.json.impl.Jackson3Provider
   ```

4. **引入开关仲裁**(详见 §5):`isAvailable()` 感知三态开关;`getPriority` 同步改为**显式优先级表**。

5. **模块内自测**:同一套快照断言分别对 `JacksonProvider` / `Jackson3Provider` 运行,逐步消除行为差异。

- **交付物**:`json-jackson` 模块内双 Provider 共存,开关默认保持 Jackson 2 生效,**现有下游零影响**。

### 阶段三:切换默认实现(可回滚)

- **观察期**:默认 `mode=auto`(见 §5.1)。发布后默认行为仍为 Jackson 2(因下游无 Jackson 3 依赖),不受影响;**预演环境**显式加 Jackson 3 依赖 + `mode=jackson3` 验证。
- **切换默认**:确认稳定后,把 Jackson 3 依赖改为 compile,使默认 classpath 携带 Jackson 3,`auto` 模式自动选中 Jackson 3 Provider。原方案另含“Jackson 2 改 optional”的一半(合称**对调**),因其对下游具破坏性,**实际未随本阶段实施**,归入阶段四——实施结果见 §11(2026-09-11)。
- **回滚**:一行操作即可——`-Dautil.json.jackson.mode=jackson2`(强制 Jackson 2),或把 pom 依赖 scope 换回。
- **全项目(含 `autil-core` / `autil-test` / `autil-incubator`)跑一遍测试与构建**。
- **交付物**:默认实现切换为 Jackson 3 + 全项目回归通过 + 回滚开关文档。

### 阶段四:清理与归档

- 稳定运行一段观察期后,删除 `JacksonProvider`(Jackson 2 实现)、Jackson 2 依赖、SPI 中对应条目;`JacksonMode` 移除 `JACKSON2` 分支(或整个开关)。
- 在 CHANGELOG / 文档记录迁移说明。

---

## 5. Provider 仲裁与开关机制(核心)

### 5.1 三态开关 `JacksonMode`

通过 JVM 系统属性 `-Dautil.json.jackson.mode=<值>` 控制,三态:

| 取值 | 语义 | 典型用途 |
|---|---|---|
| `auto`(**默认**) | 自适应:classpath 存在 `tools.jackson`(Jackson 3)则启用 Jackson 3,否则回落 Jackson 2 | 常规发布、默认行为 |
| `jackson3` | **显式启用** Jackson 3 | 迁移预演、灰度验证 |
| `jackson2` | **显式回退**到 Jackson 2 | 回滚锚点、兼容排障 |

> **同时满足「暂时显式启用」与「自适应降级/升级」**:需要确定性时显式指定;不需要时 `auto` 自动选择,且随 classpath 有无 Jackson 3 自动升降级。

```java
public enum JacksonMode {
    AUTO, JACKSON3, JACKSON2;

    static JacksonMode resolve() {
        String v = System.getProperty("autil.json.jackson.mode");
        if (v == null || v.isBlank() || "auto".equalsIgnoreCase(v)) return AUTO;
        if ("jackson3".equalsIgnoreCase(v)) return JACKSON3;
        if ("jackson2".equalsIgnoreCase(v)) return JACKSON2;
        return AUTO; // 未知取值,安全兜底
    }

    static boolean isJackson3Active() {
        JacksonMode m = resolve();
        if (m == JACKSON3) return true;
        if (m == JACKSON2) return false;
        return canLoad("tools.jackson.databind.ObjectMapper"); // auto:自适应探测
    }

    static boolean isJackson2Active() {
        return resolve() != JACKSON3; // 默认可用,仅强制 jackson3 时关闭
    }

    private static boolean canLoad(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false; // classpath 无 Jackson 3 → 回落 Jackson 2
        }
    }
}
```

### 5.2 仲裁逻辑:isAvailable + 显式优先级表

- `Jackson3Provider.isAvailable()` = `JacksonMode.isJackson3Active()`
- `JacksonProvider.isAvailable()` = `JacksonMode.isJackson2Active()`(默认 true,仅强制 `jackson3` 时 false)

`JSONUtil` 现有静态块逻辑不变(收集 `isAvailable()==true` 的 Provider → 按优先级排序 → 取第一个),`getPriority` 改为**显式优先级表**,不再依赖类名子串:

```java
// JSONUtil.getPriority 改造(阶段二同步落地)
private static final Map<String, Integer> PRIORITIES = Map.of(
    "com.acanx.util.json.impl.Jackson3Provider", 4,
    "com.acanx.util.json.impl.JacksonProvider",  3,
    "com.acanx.util.json.impl.GsonProvider",     2,
    "com.acanx.util.json.impl.FastJSONProvider", 1
);

private static int getPriority(String className) {
    return PRIORITIES.getOrDefault(className, 0);
}
```

**仲裁结果一览**:

| 开关 / classpath | Jackson3Provider.isAvailable | JacksonProvider.isAvailable | 生效 Provider |
|---|---|---|---|
| `jackson3` | ✅ | ❌ | **Jackson3** |
| `jackson2` | ❌ | ✅ | **Jackson2** |
| `auto` + 有 Jackson 3 依赖 | ✅ | ✅ | **Jackson3**(优先级 4 > 3) |
| `auto` + 无 Jackson 3 依赖 | ❌ | ✅ | **Jackson2** |

> `auto` 模式下当两者都可用时,由显式优先级表(4 > 3)稳定选择 Jackson 3;**无需改动任何业务代码即可完成默认实现切换**,切换/回滚都只是一行配置。

### 5.3 依赖 scope 与类加载安全性

- **Jackson 2 保持 compile**:现有下游依赖 `json-jackson` 获得 Jackson 2 实现,不能变 optional(阶段四清理前不清除)。
- **Jackson 3 改为 compile(阶段三,issue #193)**:取消 `optional`,默认随 `json-jackson` 传递给下游,使 `auto` 模式在下游默认选中 Jackson 3 实现;回退方式为 `-Dautil.json.jackson.mode=jackson2`,或恢复该依赖的 `optional`。
- **类加载安全性**:`Jackson3Provider` 编译期引用 `tools.jackson.*`;当开关未启用时,`isAvailable()` 返回 false,Provider **不会被实例化**,其字段/方法签名中的 `tools.jackson` 类型不会被 JVM 解析(惰性加载),**不会抛 `NoClassDefFoundError`**。`isAvailable()` 内部用 `Class.forName` + catch 探测,天然免疫缺失依赖。
- **annotations 版本约束(issue #176)**:Jackson 3(tools.jackson 3.2.2)复用 `com.fasterxml.jackson.core:jackson-annotations`(2.x 坐标),但要求 **annotations ≥ 2.22**。json-jackson 已**显式声明 annotations 2.22 为 compile 依赖**(非 optional),保证下游默认获得满足要求的版本;同时在 SPI 加载时(`Jackson3Provider.isAvailable()` 经 `Jackson3Environment`)探测实际版本——显式 `jackson3` 模式不足即抛清晰异常,`auto` 模式不足打印警告并回落 Jackson 2,替代原始 `NoClassDefFoundError`。
- **切换默认(阶段三)的本质**:Jackson 3 依赖改 compile(已实施,见 §11)+ `mode` 默认 `auto`。原方案中的“对调”另一半(Jackson 2 改 optional)对现有下游有破坏性(下游将失去 Jackson 2 实现),不与本阶段捆绑,保留至阶段四清理时一并处理。

### 5.4 开关使用示例

```bash
# 默认(自适应):classpath 有 Jackson 3 → Jackson 3(阶段三起,编译期依赖已传递到下游)
mvn test

# 预演/显式启用 Jackson 3(阶段三起 Jackson 3 已随依赖传递,通常无需自行引入)
mvn test -Dautil.json.jackson.mode=jackson3

# 排障/回滚:强制 Jackson 2
mvn test -Dautil.json.jackson.mode=jackson2
```

---

## 6. 关键设计决策

### 6.1 为什么同模块双 Provider,而不是新增独立 `json-jackson3` 模块

- **前提**:Jackson 2/3 包名与 groupId 完全隔离,同 classpath 共存无类冲突(见 §2.2),这是同模块方案的技术基础。
- **约束**:项目不希望新增 maven 模块——避免模块膨胀、依赖管理复杂化、发布链路变长。
- **等价回滚能力**:原独立模块方案靠"旧模块天然是回滚锚点";同模块方案靠「`optional` 依赖 + 三态开关」实现同样的回滚,切换/回滚都是**一行配置**,且更细粒度(可在运行时按 JVM 参数切换,无需换 jar)。
- **权衡**:同模块会把双 Jackson 依赖带入模块构建,依赖体积略增;用 `optional` 控制传递,默认对下游零影响,迁移完成清理即可。

### 6.2 为什么三态开关(而非纯显式启用)

- **纯显式启用**只能满足"我要 Jackson 3",无法应对"默认该用哪个"——若默认就选 Jackson 3,回滚要靠改配置,且首次升级无法自动判定环境。
- **三态开关**把「显式确定性」与「自适应」合一:`auto` 默认自适应(有则升、无则降),`jackson3`/`jackson2` 显式覆盖,兼顾迁移预演、灰度、回滚三种诉求,是纯显式方案的超集。

### 6.3 优先级仲裁的显式化

现状 `getPriority` 用类名子串匹配,`JacksonProvider` / `Jackson3Provider` 类名都含 `"jackson"`,会同分导致排序不稳定。改为显式优先级表后,顺序确定,且 `jackson3 > jackson2 > gson > fastjson2`,新实现默认最高。

### 6.4 测试先行是升级的安全网

当前测试空壳,升级时无自动回归能力。阶段一的行为快照测试是**整个升级可信的前提**;阶段二对两个 Provider 跑同一套快照,是"行为一致"的判定标准。

---

## 7. Jackson 3 迁移要点

| 维度 | Jackson 2 | Jackson 3 |
|---|---|---|
| 包名 | `com.fasterxml.jackson.*` | `tools.jackson.*` |
| 核心模块 | `jackson-databind` + `jackson-datatype-jsr310` | `tools.jackson.core:jackson-databind`(jsr310 并入或独立) |
| `ObjectMapper` | 直接构造 | 构建方式/默认值有差异 |
| 版本要求 | 兼容 Java 8+ | 要求 Java 17+ |

本项目 `java.version=21`,满足 Jackson 3 的 Java 要求。迁移差异由阶段一快照测试兜底。

---

## 8. 风险登记与应对

| 风险 | 概率 | 影响 | 应对 |
|---|---|---|---|
| Jackson 3 行为差异(日期/命名/null 策略) | 中 | 中 | 阶段一快照测试锁定,阶段二双 Provider 对照逐步对齐 |
| 优先级仲裁不稳定(同名 Provider 同分) | 高 | 高 | 阶段二同步落地显式优先级表 |
| 双 Jackson 依赖共存的传递冲突(如 Jackson 3 引入 `com.fasterxml.*` 传递依赖) | 低 | 中 | 实施时核实;若有则 `<exclusions>` 排除 |
| `optional` 下启用 Jackson 3 但未引入依赖 → `NoClassDefFoundError` | 低 | 中 | `isAvailable()` 用 `Class.forName` 探测 + 文档提示;未启用不会实例化 |
| Jackson 3 需 jackson-annotations ≥ 2.22,下游版本不足 → 序列化 `NoClassDefFoundError` | 中 | 高 | 显式声明 annotations 2.22 为 compile 依赖(传递保证);SPI 加载时 `Jackson3Environment` 探测版本,不足时显式模式抛清晰异常 / auto 模式警告回落(issue #176) |
| 开关拼写/取值错误 | 低 | 低 | `resolve()` 未知取值安全兜底为 `auto` |
| 下游项目升级后报错 | 中 | 高 | 开关回滚(一行 `jackson2`);依赖 scope 回退;观察期 |
| 无回归测试网 | 高 | 高 | 阶段一补行为快照测试 |
| 同模块双依赖导致 jar 体积增大 | 低 | 低 | `optional` 隔离;阶段四清理旧依赖 |

---

## 9. 验收标准

- [ ] 阶段一:行为快照测试全绿(Jackson 2 基线)
- [ ] 阶段二:不新增模块,`json-jackson` 内 `JacksonProvider` / `Jackson3Provider` 双 Provider 共存;`getPriority` 为显式优先级表;开关默认 `auto` 且默认行为为 Jackson 2,现有下游零影响
- [ ] 阶段二:三个开关取值分别验证生效 Provider(`jackson3` → Jackson3,`jackson2` → Jackson2,`auto` 有/无 Jackson 3 依赖各验一次)
- [ ] 阶段三:依赖 scope 切换后默认实现为 Jackson 3,全项目构建与测试通过;回滚开关 `jackson2` 验证有效(Jackson 3 改 compile 已实施,见 §11;Jackson 2 改 optional 未实施,归入阶段四)
- [ ] 阶段四:旧依赖/旧 Provider 删除后全绿

---

## 10. 待办 / 下一步

1. 评审本方案，确认：三态开关命名（`autil.json.jackson.mode`）、`optional` 依赖策略、阶段三依赖 scope 对调的时机。
2. 排期阶段一（行为快照测试），这是后续一切的前提。
3. 阶段二落地时，与 `JsonFeatureProposal.md` / `HttpApiJsonProposal.md` 的实施顺序保持一致（Feature 抽象 → 通用接口 → 换实现）。

---

## 11. 实施记录

### 2026-08-21：阶段一 + 阶段二已落地

**已实施：**

- ✅ 阶段一：行为快照测试全绿（Jackson 2 基线）——重写 `JacksonUtilTest` / `JacksonProviderTest`，新增 `JacksonFileUtilTest`，全部改为 JUnit 5 断言（替换空壳/打印式测试）；`JSONProviderTest` 空壳已补实。
- ✅ 阶段二：不新增模块，`json-jackson` 内 `JacksonProvider` / `Jackson3Provider` 双 Provider 共存（SPI 双注册）；`JSONUtil.getPriority` 改为显式优先级表（jackson3=4 > jackson2=3 > gson=2 > fastjson2=1）；三态开关 `JacksonMode`（`-Dautil.json.jackson.mode=auto|jackson3|jackson2`）默认 `auto`；现有下游零影响（Jackson 3 依赖 `optional`，不传递）。
- ✅ 阶段二验收：新增 `JacksonModeTest`（四态验证）、`Jackson3UtilTest` / `Jackson3ProviderTest`（与 Jackson 2 同一套断言对照）。

**关键实现要点：**

- Jackson 3 依赖：`tools.jackson.core:jackson-databind:3.2.2`（`optional`）；jsr310 已合入 databind（`tools.jackson.databind.ext.javatime`），无需单独依赖；注解仍为 `com.fasterxml.jackson.annotation`（2.22，与 Jackson 2 共用同一 artifact，无类冲突）。
- 类加载安全：`Jackson3Provider` 不持有任何 `tools.jackson.*` 字段，Jackson 3 API 全部经 `Jackson3Util` 方法体内惰性调用；classpath 无 Jackson 3 时 `isAvailable()` 返回 false，不会 `NoClassDefFoundError`。
- 行为对齐：`parseObject(String, Type/TypeReference)` 沿用 Jackson 2 共享 MAPPER 的**下划线策略**语义；自定义 LocalDateTime 格式（`yyyy-MM-dd'T'HH:mm:ss.SSSSSS`）经 `SimpleModule` 注册，Jackson 3 内置 `JavaTimeInitializer` 先注册、用户模块后注册，自定义格式生效。
- 开关使用：`mvn test -Dautil.json.jackson.mode=jackson3`（预演）/ `jackson2`（回滚）/ 默认 `auto`。

**未实施（待决策）：**

- ✅ 阶段三（Jackson 3 侧）：`tools.jackson.core:jackson-databind` 取消 `optional`，改为 compile 正常传递——已于 2026-09-11 落地（issue #193，见下）。
- ⏳ 阶段三（Jackson 2 侧）：`com.fasterxml.jackson.core:jackson-databind` 改 `optional`（原“对调”方案的另一半）。该改动会让现有下游失去 Jackson 2 实现，具破坏性，归入阶段四清理时一并处理。
- ⏳ 阶段四：观察期结束后删除 `JacksonProvider` / Jackson 2 依赖 / SPI 旧条目。
- ⏳ `auto + 无 Jackson 3 依赖` 场景的验收：在本模块无法模拟（Jackson 3 现为 compile 依赖，对模块自身测试始终可见），由下游无 Jackson 依赖模块（json-fastjson / json-gson）与 CI 参数化覆盖。
- ⏳ `JSONSerialization.serialize/deserialize`：随 `HttpApiJsonProposal.md` 实施顺序落地（接口尚未定义）。

### 2026-08-24：修复 issue #176（annotations 版本约束）

**问题**：tools.jackson 3.2.2 需要 jackson-annotations ≥ 2.22；下游若被其他依赖将 annotations 覆盖为旧版本，启用 Jackson 3 序列化时直接 `NoClassDefFoundError`。

**修复（三管齐下）**：

- ✅ pom：父 pom 新增 `jackson.annotations.version=2.22` 属性（annotations 版本线独立于 databind/core，无 2.22.2）；json-jackson 显式声明 `com.fasterxml.jackson.core:jackson-annotations` 为 **compile（非 optional）**，与 databind 2.x 传递版本一致，保证下游默认获得满足要求的版本。
- ✅ 运行时探测：新增 `Jackson3Environment`（版本解析自 jar MANIFEST `Implementation-Version`，无法解析时放行不误伤）；`Jackson3Provider.isAvailable()` SPI 加载时探测——显式 `jackson3` 模式不足抛清晰异常（含升级/回退指引），`auto` 模式不足警告并回落 Jackson 2；`Jackson3Util` 静态初始化兜底校验。
- ✅ 测试：新增 `Jackson3EnvironmentTest`（最低版本边界 2.22.0/2.22 通过、2.21.9/2.13 拒绝、null 放行、当前环境解析与校验不抛）。
- ✅ 文档：本文件 §5.3 / §8 同步更新。

### 2026-09-11：阶段三（Jackson 3 侧）落地（issue #193）

**问题**：`json-jackson` 中 `tools.jackson.core:jackson-databind` 标记了 `optional`，不向下游传递。下游 classpath 因此始终没有 Jackson 3，`auto` 模式只能选中 Jackson 2，阶段二的“切换默认”永远无法生效。

**改动**：

- ✅ pom：`autil-json/json-jackson/pom.xml` 移除该依赖的 `<optional>true</optional>`（`<scope>` 未显式声明，即 Maven 默认的 `compile`），Jackson 3 改为正常传递；同时重写上方注释，说明取消 optional 的原因、`auto` 模式的仲裁依据（优先级表 jackson3=4 > jackson2=3）与回退方式。
- ✅ 注释同步：`JacksonModeTest` / `Jackson3ProviderTest` 中“optional 依赖”的表述改为“compile 依赖”。

**行为变化（下游可见）**：

- 下游依赖 `json-jackson` 将**默认获得** `tools.jackson.core:jackson-databind:3.2.2` 及其传递依赖（`jackson-core` 3.x、`com.fasterxml.jackson.core:jackson-annotations` 2.22）。
- `auto` 模式在下游由 Jackson 2 切换为 **Jackson 3**；Jackson 2 依赖仍为 compile 保留，两者共存，注解 artifact 共用无类冲突。

**风险与回退**：

- 回退开关：`-Dautil.json.jackson.mode=jackson2` 强制 Jackson 2（`JacksonModeTest#jackson2显式回退` 覆盖）。
- 彻底回退：恢复本依赖的 `<optional>true</optional>` 并重新发布。
- annotations 版本约束沿用 issue #176 的运行时探测：下游若将 annotations 覆盖为低于 2.22 的版本，显式 `jackson3` 模式抛清晰异常，`auto` 模式警告并回落 Jackson 2。

**未实施**：Jackson 2 依赖的 `optional` 化（原“对调”方案的另一半），见上方「未实施」清单。
