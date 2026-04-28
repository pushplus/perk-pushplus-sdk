package com.perk.pushplus.spring;

import com.perk.pushplus.config.PushPlusConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * PushPlus 配置属性。前缀 {@code pushplus}。
 */
@Data
@ConfigurationProperties(prefix = "pushplus")
public class PushPlusProperties {

    /** 是否启用 PushPlus 自动装配。默认 true。 */
    private boolean enabled = true;

    /** 用户 token 或消息 token，发送消息使用。 */
    private String token;

    /** 用户 secretKey，调用开放接口使用。 */
    private String secretKey;

    /** 服务地址，默认 https://www.pushplus.plus。 */
    private String baseUrl = PushPlusConfig.DEFAULT_BASE_URL;

    /** 连接超时。默认 10s。 */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /** 读超时。默认 30s。 */
    private Duration readTimeout = Duration.ofSeconds(30);

    /** AccessKey 提前刷新秒数。默认 300。 */
    private long accessKeyRefreshAheadSeconds = 300L;

    /** 是否记录请求/响应详细日志（DEBUG 级别）。默认 false。 */
    private boolean logRequest = false;

    /**
     * 是否启用本地限流守卫。默认 true。
     * 命中 code=900（请求次数过多）后，本地短路同 token 的发送请求，避免无效调用与账号进一步受限。
     */
    private boolean rateLimitGuardEnabled = true;

    /**
     * 命中 code=900 后的本地禁推时长。{@code null} 表示使用默认策略：到次日 0 点。
     * 文档示例服务端可能持续 2 天恢复，可显式设置为 {@code 2d}。
     */
    private Duration rateLimitCooldown;

    public PushPlusConfig toConfig() {
        return PushPlusConfig.builder()
                .token(token)
                .secretKey(secretKey)
                .baseUrl(baseUrl)
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .accessKeyRefreshAheadSeconds(accessKeyRefreshAheadSeconds)
                .logRequest(logRequest)
                .rateLimitGuardEnabled(rateLimitGuardEnabled)
                .rateLimitCooldown(rateLimitCooldown)
                .build();
    }
}
