# [提案] BaseHTTP 支持原始字节请求体（bodyBytes）——二进制内容字节保真

> 提案编号：ISSUE-PROP-001
>
> 提案日期：2026-08-23
>
> 影响模块：`autil-core`（`com.acanx.util.http.BaseHTTP` / `com.acanx.util.http.HRequest`）
>
> 提案人：ACANX
>
> 目标仓库：https://github.com/ACANX/AUtil（主远端）/ https://gitee.com/ACANX/AUtil（备份）

---

## 1. 问题描述（背景）

`BaseHTTP` 是 autil-core 提供的基础 HTTP 工具（java.net.http 内核），其中请求体仅支持 **String**：

- `HRequest.body` 字段类型为 `java.lang.String`；
- `BaseHTTP.execute` 发送时固定使用 `HttpRequest.BodyPublishers.ofString(body)`（**UTF-8 编码**）。

对于**任意二进制内容**（文件直传、压缩包、图片、非 UTF-8 文本等），`String` 体存在**不可逆损坏**问题：

- 文件字节 → 字符串（任何字符集转换）→ UTF-8 编码发送，无法保证字节级还原；
- 非 ASCII 字节（≥0x80）经 UTF-8 编码会扩展为 2~3 字节，**服务端收到的内容与本地文件不一致**。

## 2. 复现场景（真实案例）

`oss-ylmf` 模块使用 `BaseHTTP` 实现阿里云 OSS 直传（V4 签名 PUT Object），上传 340KB 文本文件时被 OSS 拒绝：

```
HTTP 直传被 OSS 拒绝 status:400 body:
<Error>
  <Code>InvalidSha1</Code>
  <Message>The x-oss-hash-sha1 you specified was invalid.</Message>
  <x-oss-hash-sha1>BB73EE4AF2B862412D64F8ED4232005E29E64715</x-oss-hash-sha1>
</Error>
```

根因：请求体经 `new String(bytes, ISO_8859_1)` + `BodyPublishers.ofString`（UTF-8 发送）后，**非 ASCII 字节被扩展**，OSS 收到的文件与本地 SHA-1 不一致 → `InvalidSha1`。同一请求改用 `BodyPublishers.ofByteArray` 后立即通过（SDK 对照组逐字节一致）。

## 3. 根因分析

```java
// BaseHTTP.execute 现状（仅 String 体）
if (isBodyAllowed && StringUtil.isNotBlank(config.getBody())) {
    requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(config.getBody()));
}
```

`BodyPublishers.ofString(String)` 的语义是"把字符串按 UTF-8 编码为字节"。二进制字节流与 Java `String` 之间**不存在字符集无关的双射**（UTF-8 非法序列会被替换、ISO-8859-1 字符再编码会膨胀），因此 **String 体在协议层无法承载任意二进制**。这是 API 设计缺口，而非调用方用法问题。

## 4. 修改方案（怎么改）

在不破坏现有 API 的前提下，为请求体增加**原始字节通道**：

| 层 | 改动 |
| --- | --- |
| `HRequest` | 新增字段 `byte[] bodyBytes`；新增 getter `getBodyBytes()`；Builder 新增 `bodyBytes(byte[])` |
| `BaseHTTP.execute` | 请求体发送改为三分支：**字节体优先**（`ofByteArray`）→ String 体（`ofString`）→ 无体（`noBody`） |

优先级语义（互斥明确）：`bodyBytes` 非空（长度 >0）时使用原始字节体，`body(String)` 被忽略；两者都未设置时维持 `noBody`。

## 5. 改动明细（改了什么）

### 5.1 `HRequest.java`

```java
// 新增字段
private byte[] bodyBytes;

// 新增 getter
public byte[] getBodyBytes() { return bodyBytes; }

// Builder 新增方法
public Builder bodyBytes(byte[] bodyBytes) {
    config.bodyBytes = bodyBytes;
    return this;
}
```

### 5.2 `BaseHTTP.java`（execute 请求体段落）

```java
boolean isBodyAllowed = (!HTTPConst.GET.equalsIgnoreCase(method) && !HTTPConst.DELETE.equalsIgnoreCase(method));
if (isBodyAllowed && config.getBodyBytes() != null && config.getBodyBytes().length > 0) {
    // 原始字节请求体（二进制保真，如文件直传）；与 String 体互斥，优先使用
    requestBuilder.method(method, HttpRequest.BodyPublishers.ofByteArray(config.getBodyBytes()));
} else if (isBodyAllowed && StringUtil.isNotBlank(config.getBody())) {
    requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(config.getBody()));
} else {
    requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
}
```

## 6. 兼容性（如何兼容）

| 维度 | 说明 |
| --- | --- |
| API 兼容 | 现有公开方法（`body(String)`、`getBody()`、快捷方法 `get/post/postForm` 等）**全部保留**，签名不变 |
| 行为兼容 | 新字段默认 `null` → 既有调用方走原 String/无体分支，**运行时行为零变化** |
| 字节兼容 | 发送的请求头、URL 组装、超时、Cookie、响应解析逻辑均未触碰 |
| 编译兼容 | 仅新增字段/方法，无删除、无重载歧义（`body(String)` 与 `bodyBytes(byte[])` 参数类型不同） |
| 传递依赖 | `autil-core` 仍只依赖 JDK（java.net.http），无新增第三方依赖 |

## 7. 扩展性（如何扩展）

`bodyBytes(byte[])` 是"原始字节"能力的第一层，后续可按同一模式演进：

| 扩展方向 | 方案 | 收益 |
| --- | --- | --- |
| 大文件流式 | Builder 增加 `bodyStream(InputStream)` → `BodyPublishers.ofInputStream`（配合 `contentLength`） | 避免整文件内存驻留 |
| 文件直发 | Builder 增加 `bodyFile(Path)` → `BodyPublishers.ofFile` | 零拷贝、内存友好 |
| 完全自定义 | Builder 增加 `bodyPublisher(BodyPublisher)` 直通 java.net.http | 最灵活，覆盖一切场景 |
| 响应侧 | 增加字节响应能力（`BodyHandlers.ofByteArray` 变体或 `HResponse.getBodyBytes()`） | 二进制响应（下载）对称支持 |

建议将 `bodyBytes`/后续扩展统一收敛为 `HRequest` 的"请求体策略"字段（字节体 / 流体 / 文件体三选一），`BaseHTTP` 只做策略分发，保持内核稳定。

## 8. 使用示例

```java
// 二进制文件直传（PUT，原始字节保真）
byte[] content = Files.readAllBytes(path);
HRequest request = HRequest.builder()
        .method("PUT")
        .url("http://host/bucket/object")
        .headers(headers)          // 含 Content-Type、签名头等
        .bodyBytes(content)        // 原始字节体
        .readTimeout(600000)       // 大文件放宽超时
        .build();
HResponse response = BaseHTTP.getHttpResponse(request);
```

## 9. 验证（测试）

- **单元验证**：`mvn install -pl autil-core -am -DskipTests` 构建通过；`autil-core` 既有测试不受影响；
- **集成验证**（oss-ylmf 消费方）：
  - 二进制内容（含非 ASCII 字节，4096 字节混合序列）经 `bodyBytes` 发送，Mock 服务端捕获请求体与对照组（阿里云官方 SDK）**逐字节一致**；
  - 真实 OSS 直传（V4 签名 PUT）：修复前 `InvalidSha1`，修复后上传成功；
  - 消费方全量单测 **66 用例 0 失败**（含 String 体既有调用路径回归）。
- **回归验证**：既有 `body(String)` 调用方（115 Open API 等）请求体/响应行为不变。

## 10. 后续建议

1. 本提案改动已在本地 `autil-core` 完成并验证，等待本 ISSUE 评审后合入上游；
2. 合入后建议发布 `1.3.1`（或下一个 minor），并同步更新 `Docs/` 中 BaseHTTP 的用法文档；
3. 大文件流式能力（`bodyStream`）建议作为独立 ISSUE 跟踪，与本次字节体改动解耦。

---

## 附：本次提案对应的本地改动文件

| 文件 | 状态 |
| --- | --- |
| `autil-core/src/main/java/com/acanx/util/http/HRequest.java` | 已修改（新增 bodyBytes 能力） |
| `autil-core/src/main/java/com/acanx/util/http/BaseHTTP.java` | 已修改（字节体优先发送） |
