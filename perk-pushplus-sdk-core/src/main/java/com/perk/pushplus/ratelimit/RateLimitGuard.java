package com.perk.pushplus.ratelimit;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.ErrorCode;
import com.perk.pushplus.exception.PushPlusException;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地限流守卫：当 PushPlus 服务端返回 {@link ErrorCode#RATE_LIMITED}（code=900）时，
 * 在内存里按 {@code token} 维度记录"解禁时间"，期间任何发送类调用都会直接抛
 * {@link PushPlusException}（code=900），避免继续打到上游浪费请求并加重账号限制。
 *
 * <p>对应官方文档建议：
 * <a href="https://www.pushplus.plus/doc/guide/code.html">接口返回码说明 - 根据返回码减少无用请求</a>。
 *
 * <p><b>禁推时长</b>：默认到"次日 0 点"自动解禁；也可以通过
 * {@link PushPlusConfig#getRateLimitCooldown()} 配置一个固定时长（设置后优先生效）。
 *
 * <p><b>线程安全</b>：内部使用 {@link ConcurrentHashMap}，多实例可并发使用；本类无状态以外的副作用。
 *
 * <p><b>仅本地视角</b>：服务端实际禁推时长可能与本地估算不同（文档示例为 2 天）。
 * 本守卫只是把客户端视为"今日已被限流"以避免无效请求，
 * 解禁后若服务端仍在限流，会再次返回 900 并重新触发本地短路。
 */
@Slf4j
public class RateLimitGuard {

    private final PushPlusConfig config;
    private final Clock clock;
    private final ConcurrentHashMap<String, Instant> blockedUntil = new ConcurrentHashMap<>();

    public RateLimitGuard(PushPlusConfig config) {
        this(config, Clock.systemDefaultZone());
    }

    /** 仅供单元测试覆写时钟使用。 */
    public RateLimitGuard(PushPlusConfig config, Clock clock) {
        this.config = config;
        this.clock = clock;
    }

    /**
     * 在发起发送类请求前调用：若当前 token 处于限流期，直接抛 {@link PushPlusException}
     * （code = {@link ErrorCode#RATE_LIMITED}），不会发起 HTTP。
     *
     * <p>开关关闭（{@link PushPlusConfig#isRateLimitGuardEnabled()} = false）时直接放行。
     */
    public void check(String token) {
        if (!config.isRateLimitGuardEnabled()) {
            return;
        }
        String key = normalize(token);
        if (key == null) {
            return;
        }
        Instant until = blockedUntil.get(key);
        if (until == null) {
            return;
        }
        Instant now = clock.instant();
        if (now.isBefore(until)) {
            throw new PushPlusException(ErrorCode.RATE_LIMITED.getCode(),
                    "PushPlus 本地限流守卫：当前 token 已命中 code=900，"
                            + "在 " + until + " 之前不再发起请求（请参考官方文档减少无用请求）");
        }
        blockedUntil.remove(key, until);
    }

    /**
     * 在收到服务端 code=900 后调用：登记限流状态，直到 {@link #cooldownUntil(Instant)} 到期。
     */
    public void markBlocked(String token) {
        if (!config.isRateLimitGuardEnabled()) {
            return;
        }
        String key = normalize(token);
        if (key == null) {
            return;
        }
        Instant now = clock.instant();
        Instant until = cooldownUntil(now);
        blockedUntil.put(key, until);
        log.warn("[pushplus] 命中 code=900，本地限流守卫将拒绝该 token 的发送请求至 {}（token 末位: {}）",
                until, tail(key));
    }

    /** 仅供测试或运维手动清除（例如已确认服务端解禁）。 */
    public void clear(String token) {
        String key = normalize(token);
        if (key != null) {
            blockedUntil.remove(key);
        }
    }

    /** 返回该 token 的解禁时间；未被限流则为 null。 */
    public Instant blockedUntil(String token) {
        String key = normalize(token);
        return key == null ? null : blockedUntil.get(key);
    }

    /**
     * 计算解禁时间：
     * <ul>
     *   <li>{@link PushPlusConfig#getRateLimitCooldown()} 不为空时，使用 {@code now + cooldown}；</li>
     *   <li>否则使用"系统默认时区的次日 0 点"。</li>
     * </ul>
     */
    private Instant cooldownUntil(Instant now) {
        Duration cooldown = config.getRateLimitCooldown();
        if (cooldown != null && !cooldown.isZero() && !cooldown.isNegative()) {
            return now.plus(cooldown);
        }
        ZoneId zone = clock.getZone();
        LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
        return tomorrow.atStartOfDay(zone).toInstant();
    }

    private static String normalize(String token) {
        if (token == null) {
            return null;
        }
        String t = token.trim();
        return t.isEmpty() ? null : t;
    }

    private static String tail(String token) {
        if (token.length() <= 4) {
            return "****";
        }
        return "****" + token.substring(token.length() - 4);
    }
}
