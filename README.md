# perk-pushplus-sdk

[PushPlus(推送加)](https://www.pushplus.plus) 官方接口的 Java SDK，覆盖**消息发送接口**与**全部开放接口**。

- 纯 Java SDK + Spring Boot Starter 双模块设计
- Java 21、JDK 内置 `HttpClient`（无 OkHttp 等额外重依赖）
- AccessKey **自动获取、缓存、过期前刷新、失效自动重试**，调用方无感知
- **本地限流守卫**：发送接口命中 `code=900`（请求次数过多）时自动短路同 token 的后续调用，避免无效请求与账号进一步受限（[官方建议](https://www.pushplus.plus/doc/guide/code.html)）
- 单条 `/send`、多渠道 `/batchSend`、消息回调（`message_complate` / `add_topic_user` / `add_friend`）类型化解析
- 全部开放接口：消息、用户、消息令牌、群组、群组用户、好友、Webhook、公众号/企业微信/邮箱渠道、ClawBot、功能设置、预处理
- 完善的 Builder 模式与强类型枚举（`Channel`、`Template`、`SendStatus`、`WebhookType`、`CallbackEvent`、`ErrorCode`）

## 模块说明

| 模块 | 说明 |
| --- | --- |
| `perk-pushplus-sdk-core` | 核心 SDK，纯 Java，无任何框架依赖，可在任意 Java 项目中使用 |
| `perk-pushplus-sdk-spring-boot-starter` | Spring Boot 自动装配，配置即用 |
| `perk-pushplus-sdk-example` | 使用示例（不发布） |

## 快速开始

### 1. 引入依赖

普通 Java 项目：

```xml
<dependency>
    <groupId>com.perk-net</groupId>
    <artifactId>perk-pushplus-sdk-core</artifactId>
    <version>1.0.4</version>
</dependency>
```

Spring Boot 项目：

```xml
<dependency>
    <groupId>com.perk-net</groupId>
    <artifactId>perk-pushplus-sdk-spring-boot-starter</artifactId>
    <version>1.0.4</version>
</dependency>
```

### 2. 发送一条消息（最简）

```java
PushPlusClient client = PushPlusClient.builder()
        .token("your_user_token")        // 必填
        .secretKey("your_secret_key")    // 调用开放接口才需要
        .build();

String shortCode = client.sendSimple("标题", "Hello PushPlus");
```

### 3. 使用 Builder 发送 Markdown / HTML / JSON 消息

```java
client.send(SendRequest.builder()
        .title("部署完成")
        .content("# v1.0.0\n- env: prod")
        .template(Template.MARKDOWN)
        .topic("ops")                    // 群组编码
        .channel(Channel.WECHAT)         // 默认就是 WECHAT，可省略
        .callbackUrl("https://you/cb")   // 异步回调
        .timestamp(System.currentTimeMillis() + 5000) // 时效控制
        .build());
```

### 4. 多渠道发送（`/batchSend`）

```java
List<BatchSendResult> result = client.batchSend(BatchSendRequest.builder()
        .title("多渠道告警")
        .content("CPU > 90%")
        .channel(Channel.WECHAT).option("")
        .channel(Channel.WEBHOOK).option("bark")
        .channel(Channel.EXTENSION).option("")
        .build());
```

`channel(...)` 与 `option(...)` 可累计调用，SDK 自动用逗号拼接，与官方文档示例语义一致。

### 5. 异步发送

```java
CompletableFuture<String> future = client.sendAsync(
        SendRequest.builder().title("t").content("c").build());
```

## 开放接口（全量）

需要在 PushPlus 后台「开发设置」中：开启开放接口、配置 `secretKey`、把调用方所在公网 IP 加入安全 IP 列表。

```java
PushPlusClient client = PushPlusClient.builder()
        .token("user_token")
        .secretKey("xxx")
        .build();

// AccessKey 完全自动管理 —— 直接调用就好
UserInfo me = client.getUser().myInfo();
PageResult<MessageItem> msgs = client.getOpenMessage().list(PageQuery.of(1, 20));
SendMessageResult r = client.getOpenMessage().queryResult(shortCode);

// 群组
PageResult<TopicItem> topics = client.getTopic().list(TopicListQuery.of(1, 20, 0));
TopicQrCode qr = client.getTopic().qrCode(/*topicId*/ 1, /*second*/ 86400, /*scanCount*/ -1);

// Webhook 渠道配置
client.getWebhook().add(WebhookSaveRequest.builder()
        .webhookCode("bark")
        .webhookName("我的 Bark")
        .webhookType(WebhookType.BARK.getCode())
        .webhookUrl("https://api.day.app/xxxx")
        .build());

// 功能设置
client.getSetting().changeIsSend(1);
```

各 API 一览：

| getter | 对应文档章节 |
| --- | --- |
| `client.getMessage()` | 二/三 发送消息接口 |
| `client.getAccessKey()` | 一 获取 AccessKey（一般无需手动调用） |
| `client.getOpenMessage()` | 二 消息接口（开放） |
| `client.getUser()` | 三 用户接口 |
| `client.getMessageToken()` | 四 消息 token 接口 |
| `client.getTopic()` | 五 群组接口 |
| `client.getTopicUser()` | 六 群组用户接口 |
| `client.getWebhook()` | 七 渠道配置 - webhook |
| `client.getChannel()` | 七 渠道配置 - 公众号/企业微信/邮箱 |
| `client.getClawBot()` | 八 微信 ClawBot 接口 |
| `client.getSetting()` | 九 功能设置接口 |
| `client.getFriend()` | 十 好友功能接口 |
| `client.getPre()` | 十一 预处理信息接口 |

## 消息回调解析

PushPlus 在消息发送完成、群组新增用户、新增好友时会回调你预置的 URL。SDK 提供类型安全的解析器：

```java
@PostMapping("/pushplus/callback")
public String callback(@RequestBody String body) {
    CallbackPayload p = CallbackParser.parse(body);
    switch (p.getEvent()) {
        case MESSAGE_COMPLETE -> handle(p.getMessageInfo());      // SendStatus 枚举可直接拿
        case ADD_TOPIC_USER  -> handle(p.getTopicUserInfo());
        case ADD_FRIEND      -> handle(p.getFriendInfo(), p.getQrCode());
    }
    return "ok";
}
```

## Spring Boot 集成

`application.yml`：

```yaml
pushplus:
  token: ${PUSHPLUS_TOKEN}
  secret-key: ${PUSHPLUS_SECRET_KEY}   # 调用开放接口必填
  base-url: https://www.pushplus.plus
  connect-timeout: 10s
  read-timeout: 30s
  access-key-refresh-ahead-seconds: 300
  log-request: false
  rate-limit-guard-enabled: true       # 命中 code=900 后本地短路，默认开
  rate-limit-cooldown:                 # 留空表示禁推到次日 0 点；可显式写 2d / 24h 等
```

随后即可注入：

```java
@Service
public class NotifyService {

    private final PushPlusClient pushPlus;

    public NotifyService(PushPlusClient pushPlus) {
        this.pushPlus = pushPlus;
    }

    public void onAlert(String msg) {
        pushPlus.sendSimple("告警", msg);
    }
}
```

如需自定义 HTTP 实现，往容器中放一个 `HttpRequester` Bean 即可被自动注入到 `PushPlusClient`。

## 配置项

| 字段 | 默认 | 说明 |
| --- | --- | --- |
| `token` | – | 用户 token / 消息 token，发送消息使用 |
| `secretKey` | – | 用户 secretKey，调用开放接口使用 |
| `baseUrl` | `https://www.pushplus.plus` | 服务地址 |
| `connectTimeout` | 10s | 连接超时 |
| `readTimeout` | 30s | 读超时 |
| `accessKeyRefreshAheadSeconds` | 300 | AccessKey 提前刷新秒数（PushPlus 文档说明老 key 在新 key 生成后 5 分钟内仍可用） |
| `logRequest` | false | 开启 DEBUG 级别的请求/响应日志 |
| `rateLimitGuardEnabled` | true | 是否启用本地限流守卫；命中 `code=900` 后短路同 token 的发送请求 |
| `rateLimitCooldown` | – | 命中 `code=900` 后的本地禁推时长；为空表示到"次日 0 点"，可显式设置 `Duration.ofDays(2)` |

## 异常处理

所有错误都会包装成 `PushPlusException`：

- HTTP 调用失败：`code` 为 HTTP 状态码
- 业务接口返回 `code != 200`：`code` 为业务码、`message` 为业务消息
- SDK 参数校验失败：`code = -1`
- **本地限流守卫命中**（不发起 HTTP）：`code = 900`、`message` 包含 "本地限流守卫" 字样

`ErrorCode` 枚举已经把官方文档的全部业务码语义化（`OK / NOT_LOGIN / UNAUTHORIZED / IP_FORBIDDEN / SERVER_ERROR / DATA_ERROR / FORBIDDEN_VIEW / INSUFFICIENT_POINTS / RATE_LIMITED / INVALID_TOKEN / NOT_VERIFIED / VALIDATION_ERROR`），无需再用魔法数字判断。

```java
try {
    client.sendSimple("t", "c");
} catch (PushPlusException e) {
    if (e.isRateLimited()) {                  // 等价于 e.getErrorCode() == ErrorCode.RATE_LIMITED
        log.warn("PushPlus 限流，今天暂停推送: {}", e.getMessage());
        return;
    }
    switch (e.getErrorCode()) {
        case INVALID_TOKEN     -> log.error("token 错误，立即排查配置");
        case NOT_VERIFIED      -> log.error("账号未实名认证");
        case INSUFFICIENT_POINTS -> log.warn("积分不足");
        default -> log.warn("PushPlus 失败: code={}, msg={}", e.getCode(), e.getMessage());
    }
}
```

参考：[PushPlus 接口返回码说明](https://www.pushplus.plus/doc/guide/code.html)。

## 限流守卫（code=900 自动短路）

PushPlus 在请求次数过多时会返回 `code=900`，官方文档明确建议"根据返回值判断当天是否让程序继续调用发送消息接口，否则会让账号进一步受限"。SDK 默认替你做这件事：

- 任意一次 `client.send(...)` / `client.batchSend(...)` 命中 `code=900` 后，SDK 会按 token 维度记下"禁推至 X 时刻"。
- 同 token 后续发送调用不再发起 HTTP，直接抛 `PushPlusException(code=900, msg="本地限流守卫…")`。
- 默认禁推到**系统时区的次日 0 点**；通过 `rateLimitCooldown` 可改为固定时长（例如文档示例的 2 天）。
- 仅作用于 `MessageApi`（`send` / `batchSend`），开放接口不受影响。
- 进程内单例，**不跨进程共享**——多实例部署时每个进程最多被命中一次。

可观察 / 可干预：

```java
RateLimitGuard guard = client.getRateLimitGuard();

Instant until = guard.blockedUntil("user_token");   // null 表示未被限流；否则为本地解禁时间
guard.clear("user_token");                          // 例如：人工确认服务端已解禁后立即放行
```

需要完全关闭这个行为（不推荐）：

```java
PushPlusClient client = PushPlusClient.builder()
        .token("xxx")
        .rateLimitGuardEnabled(false)
        .build();
```

或者按业务把禁推时长拉长 / 缩短：

```java
PushPlusClient client = PushPlusClient.builder()
        .token("xxx")
        .rateLimitCooldown(Duration.ofDays(2))   // 与文档示例的"2 天恢复正常"对齐
        .build();
```

## 构建与发布

```bash
./mvnw clean install     # 本地安装
./mvnw test              # 跑单元测试
./mvnw -pl perk-pushplus-sdk-example exec:java \
       -Dexec.mainClass=example.com.perk.pushplus.QuickStart  # 跑示例
```

## License

Apache License 2.0
