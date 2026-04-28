package com.perk.pushplus.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

/**
 * PushPlus SDK 全局配置。
 *
 * <p>通过 {@link #builder()} 构建实例。所有字段都有合理的默认值，
 * 仅 {@code token} 是发送消息接口必填、{@code secretKey} 是开放接口必填。</p>
 *
 * <pre>{@code
 * PushPlusConfig config = PushPlusConfig.builder()
 *     .token("your_token")
 *     .secretKey("your_secret_key")
 *     .build();
 * }</pre>
 */
@Getter
@Builder(toBuilder = true)
@ToString(exclude = {"token", "secretKey"})
public class PushPlusConfig {

    /** PushPlus 默认服务地址。 */
    public static final String DEFAULT_BASE_URL = "https://www.pushplus.plus";

    /**
     * 用户 token 或消息 token，发送消息接口默认使用。
     * 注意：获取 AccessKey 必须使用用户 token。
     */
    private final String token;

    /**
     * 用户 secretKey，调用开放接口（获取 AccessKey）必填。
     * 在 pushplus 个人中心 -> 开发设置 中配置。
     */
    private final String secretKey;

    /**
     * 服务器基础地址。默认：{@value #DEFAULT_BASE_URL}
     */
    @Builder.Default
    private final String baseUrl = DEFAULT_BASE_URL;

    /** 连接超时。默认 10 秒。 */
    @Builder.Default
    private final Duration connectTimeout = Duration.ofSeconds(10);

    /** 请求/读超时。默认 30 秒。 */
    @Builder.Default
    private final Duration readTimeout = Duration.ofSeconds(30);

    /**
     * 在 AccessKey 过期前提前多少秒刷新。默认提前 5 分钟（300 秒），
     * 文档中提到老 AccessKey 在新 AccessKey 生成后 5 分钟内仍可用，因此提前 5 分钟刷新最稳妥。
     */
    @Builder.Default
    private final long accessKeyRefreshAheadSeconds = 300L;

    /**
     * 是否启用请求/响应详细日志（DEBUG 级别）。默认关闭。
     */
    @Builder.Default
    private final boolean logRequest = false;

    /**
     * 是否启用本地限流守卫。默认开启。
     *
     * <p>开启后，当任意一次发送消息接口返回 code=900（请求次数过多）时，
     * 后续对同一 token 的发送调用会被 SDK 直接短路，不再发起 HTTP，
     * 直到 {@link #rateLimitCooldown}（默认次日 0 点）到期。</p>
     *
     * <p>对应官方文档：<a href="https://www.pushplus.plus/doc/guide/code.html">接口返回码说明</a>。</p>
     */
    @Builder.Default
    private final boolean rateLimitGuardEnabled = true;

    /**
     * 命中 code=900 后的本地禁推时长。{@code null} 表示使用默认策略：到"次日 0 点"。
     *
     * <p>注意：服务端实际禁推时长可能更长（文档示例为 2 天）。如果你已经知道账号被
     * 长期封禁，可以把它显式设置为 {@code Duration.ofDays(2)} 或更久来减少无用试探。</p>
     */
    private final Duration rateLimitCooldown;

    /**
     * 返回有效的 baseUrl，去除尾部 "/"。
     */
    public String resolveBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
