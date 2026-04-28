package com.perk.pushplus.ratelimit;

import com.perk.pushplus.PushPlusClient;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.ErrorCode;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.test.MockHttpRequester;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitGuardTest {

    private static final String RATE_LIMITED_BODY =
            "{\"code\":900,\"msg\":\"请求次数过多,减少请求后2天恢复正常\",\"data\":null}";

    private PushPlusClient newClient(PushPlusConfig config, MockHttpRequester http) {
        return PushPlusClient.builder()
                .config(config)
                .httpRequester(http)
                .build();
    }

    @Test
    void should_short_circuit_after_first_900_response() {
        PushPlusConfig config = PushPlusConfig.builder()
                .token("user_token")
                .secretKey("sk")
                .build();
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/send", 200, RATE_LIMITED_BODY);

        PushPlusClient client = newClient(config, http);

        PushPlusException first = assertThrows(PushPlusException.class,
                () -> client.sendSimple("t", "c"));
        assertEquals(900, first.getCode());
        assertEquals(ErrorCode.RATE_LIMITED, first.getErrorCode());
        assertTrue(first.isRateLimited());
        assertEquals(1, http.getRecords().size(), "首次 900 应该真正发起一次 HTTP");

        PushPlusException second = assertThrows(PushPlusException.class,
                () -> client.sendSimple("t", "c"));
        assertTrue(second.isRateLimited());
        assertTrue(second.getMessage().contains("本地限流守卫"),
                "命中后应由本地守卫直接抛出，msg 应包含本地守卫标识");
        assertEquals(1, http.getRecords().size(),
                "命中守卫后不应再发起 HTTP；当前记录数应保持不变");

        assertNotNull(client.getRateLimitGuard().blockedUntil("user_token"));
    }

    @Test
    void should_skip_guard_when_disabled() {
        PushPlusConfig config = PushPlusConfig.builder()
                .token("user_token")
                .secretKey("sk")
                .rateLimitGuardEnabled(false)
                .build();
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/send", 200, RATE_LIMITED_BODY)
                .whenPath("/send", 200, RATE_LIMITED_BODY);

        PushPlusClient client = newClient(config, http);

        assertThrows(PushPlusException.class, () -> client.sendSimple("t", "c"));
        assertThrows(PushPlusException.class, () -> client.sendSimple("t", "c"));
        assertEquals(2, http.getRecords().size(),
                "守卫关闭时，每次 send 都应真正发起 HTTP");
        assertNull(client.getRateLimitGuard().blockedUntil("user_token"));
    }

    @Test
    void should_release_after_cooldown_with_custom_clock() {
        PushPlusConfig config = PushPlusConfig.builder()
                .token("user_token")
                .secretKey("sk")
                .rateLimitCooldown(Duration.ofMinutes(30))
                .build();

        Instant t0 = Instant.parse("2026-04-28T10:00:00Z");
        MutableClock clock = new MutableClock(t0, ZoneId.of("UTC"));
        RateLimitGuard guard = new RateLimitGuard(config, clock);

        guard.markBlocked("user_token");
        assertEquals(t0.plus(Duration.ofMinutes(30)), guard.blockedUntil("user_token"));

        assertThrows(PushPlusException.class, () -> guard.check("user_token"),
                "30 分钟内应被本地守卫拒绝");

        clock.set(t0.plus(Duration.ofMinutes(31)));
        assertDoesNotThrow(() -> guard.check("user_token"),
                "冷却期结束后应自动放行");
        assertNull(guard.blockedUntil("user_token"),
                "首次放行时应清除已过期的禁推记录");
    }

    @Test
    void should_default_cooldown_to_next_day_midnight() {
        PushPlusConfig config = PushPlusConfig.builder()
                .token("user_token")
                .secretKey("sk")
                .build();

        ZoneId zone = ZoneId.of("Asia/Shanghai");
        Instant noon = Instant.parse("2026-04-28T04:00:00Z"); // = 12:00 in +08:00
        MutableClock clock = new MutableClock(noon, zone);
        RateLimitGuard guard = new RateLimitGuard(config, clock);

        guard.markBlocked("user_token");
        Instant until = guard.blockedUntil("user_token");
        assertNotNull(until);
        assertEquals(Instant.parse("2026-04-28T16:00:00Z"), until,
                "默认应禁推到 +08:00 的次日 0 点（即 UTC 16:00）");
    }

    /** 简单的可变时钟，便于在测试中推进时间。 */
    private static final class MutableClock extends Clock {
        private Instant now;
        private final ZoneId zone;

        MutableClock(Instant now, ZoneId zone) {
            this.now = now;
            this.zone = zone;
        }

        void set(Instant now) {
            this.now = now;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(now, zone);
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
