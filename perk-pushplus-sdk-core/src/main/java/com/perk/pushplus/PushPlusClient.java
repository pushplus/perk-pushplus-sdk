package com.perk.pushplus;

import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.api.AccessKeyApi;
import com.perk.pushplus.api.ChannelApi;
import com.perk.pushplus.api.ClawBotApi;
import com.perk.pushplus.api.FriendApi;
import com.perk.pushplus.api.MessageApi;
import com.perk.pushplus.api.MessageTokenApi;
import com.perk.pushplus.api.OpenMessageApi;
import com.perk.pushplus.api.PreApi;
import com.perk.pushplus.api.SettingApi;
import com.perk.pushplus.api.TopicApi;
import com.perk.pushplus.api.TopicUserApi;
import com.perk.pushplus.api.UserApi;
import com.perk.pushplus.api.WebhookApi;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.http.JdkHttpRequester;
import com.perk.pushplus.model.send.BatchSendRequest;
import com.perk.pushplus.model.send.BatchSendResult;
import com.perk.pushplus.model.send.SendRequest;
import com.perk.pushplus.ratelimit.RateLimitGuard;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * PushPlus SDK 统一入口。
 *
 * <h2>快速开始</h2>
 *
 * <pre>{@code
 * PushPlusClient client = PushPlusClient.builder()
 *     .token("your_user_token")
 *     .secretKey("your_secret_key")
 *     .build();
 *
 * // 发送消息
 * String shortCode = client.sendSimple("标题", "内容");
 *
 * // 调用开放接口（无需手动管理 AccessKey）
 * UserInfo info = client.user().myInfo();
 * }</pre>
 *
 * <p>客户端线程安全，建议作为单例长期持有。</p>
 */
public class PushPlusClient {

    @Getter private final PushPlusConfig config;
    @Getter private final HttpRequester httpRequester;
    @Getter private final AccessKeyManager accessKeyManager;
    @Getter private final RateLimitGuard rateLimitGuard;

    /* ----------------- 各 API（懒加载也可以，但创建很轻） ----------------- */
    @Getter private final MessageApi message;
    @Getter private final AccessKeyApi accessKey;
    @Getter private final OpenMessageApi openMessage;
    @Getter private final UserApi user;
    @Getter private final MessageTokenApi messageToken;
    @Getter private final TopicApi topic;
    @Getter private final TopicUserApi topicUser;
    @Getter private final FriendApi friend;
    @Getter private final WebhookApi webhook;
    @Getter private final ChannelApi channel;
    @Getter private final ClawBotApi clawBot;
    @Getter private final SettingApi setting;
    @Getter private final PreApi pre;

    private PushPlusClient(PushPlusConfig config, HttpRequester httpRequester) {
        if (config == null) {
            throw new PushPlusException("PushPlusConfig 不能为空");
        }
        this.config = config;
        this.httpRequester = httpRequester != null ? httpRequester : new JdkHttpRequester(config);
        this.rateLimitGuard = new RateLimitGuard(this.config);

        this.message = new MessageApi(this.config, this.httpRequester, this.rateLimitGuard);
        this.accessKey = new AccessKeyApi(this.config, this.httpRequester);
        this.accessKeyManager = new AccessKeyManager(this.config, this.accessKey);

        this.openMessage = new OpenMessageApi(this.config, this.httpRequester, this.accessKeyManager);
        this.user = new UserApi(this.config, this.httpRequester, this.accessKeyManager);
        this.messageToken = new MessageTokenApi(this.config, this.httpRequester, this.accessKeyManager);
        this.topic = new TopicApi(this.config, this.httpRequester, this.accessKeyManager);
        this.topicUser = new TopicUserApi(this.config, this.httpRequester, this.accessKeyManager);
        this.friend = new FriendApi(this.config, this.httpRequester, this.accessKeyManager);
        this.webhook = new WebhookApi(this.config, this.httpRequester, this.accessKeyManager);
        this.channel = new ChannelApi(this.config, this.httpRequester, this.accessKeyManager);
        this.clawBot = new ClawBotApi(this.config, this.httpRequester, this.accessKeyManager);
        this.setting = new SettingApi(this.config, this.httpRequester, this.accessKeyManager);
        this.pre = new PreApi(this.config, this.httpRequester, this.accessKeyManager);
    }

    /* =========================== 工厂 / Builder =========================== */

    public static PushPlusClient of(PushPlusConfig config) {
        return new PushPlusClient(config, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PushPlusConfig.PushPlusConfigBuilder cfg = PushPlusConfig.builder();
        private PushPlusConfig fullConfig;
        private HttpRequester httpRequester;

        public Builder token(String token) { cfg.token(token); return this; }
        public Builder secretKey(String secretKey) { cfg.secretKey(secretKey); return this; }
        public Builder baseUrl(String baseUrl) { cfg.baseUrl(baseUrl); return this; }
        public Builder connectTimeout(java.time.Duration d) { cfg.connectTimeout(d); return this; }
        public Builder readTimeout(java.time.Duration d) { cfg.readTimeout(d); return this; }
        public Builder accessKeyRefreshAheadSeconds(long s) { cfg.accessKeyRefreshAheadSeconds(s); return this; }
        public Builder logRequest(boolean enabled) { cfg.logRequest(enabled); return this; }
        public Builder rateLimitGuardEnabled(boolean enabled) { cfg.rateLimitGuardEnabled(enabled); return this; }
        public Builder rateLimitCooldown(java.time.Duration d) { cfg.rateLimitCooldown(d); return this; }
        public Builder httpRequester(HttpRequester req) { this.httpRequester = req; return this; }

        /** 直接传入完整 config，覆盖之前的 setter。 */
        public Builder config(PushPlusConfig fullConfig) { this.fullConfig = fullConfig; return this; }

        public PushPlusClient build() {
            PushPlusConfig finalCfg = fullConfig != null ? fullConfig : cfg.build();
            return new PushPlusClient(finalCfg, httpRequester);
        }
    }

    /* =========================== 便捷转发方法 =========================== */

    /** 发送一条简单消息（默认 wechat / html）。 */
    public String sendSimple(String title, String content) {
        return message.sendSimple(title, content);
    }

    /** 发送消息。 */
    public String send(SendRequest req) {
        return message.send(req);
    }

    public CompletableFuture<String> sendAsync(SendRequest req) {
        return message.sendAsync(req);
    }

    /** 多渠道发送消息。 */
    public List<BatchSendResult> batchSend(BatchSendRequest req) {
        return message.batchSend(req);
    }

    public CompletableFuture<List<BatchSendResult>> batchSendAsync(BatchSendRequest req) {
        return message.batchSendAsync(req);
    }
}
