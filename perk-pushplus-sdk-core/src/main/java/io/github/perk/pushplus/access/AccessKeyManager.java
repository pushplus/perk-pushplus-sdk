package io.github.perk.pushplus.access;

import io.github.perk.pushplus.api.AccessKeyApi;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.exception.PushPlusException;
import io.github.perk.pushplus.model.open.access.AccessKeyResult;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AccessKey 管理器。
 *
 * <p>提供线程安全的 AccessKey 缓存 + 过期前自动刷新能力。
 * 在调用任意需要 access-key 的开放接口前，{@link io.github.perk.pushplus.api.OpenAbstractApi}
 * 会自动通过本类拿到一个有效的 AccessKey。</p>
 *
 * <p>刷新策略：在 expiresIn 到期前 {@link PushPlusConfig#getAccessKeyRefreshAheadSeconds()} 秒
 * 视为过期。文档说明老 key 在新 key 生成后 5 分钟内仍可用，因此默认 300 秒提前量足够安全。</p>
 *
 * <p>线程安全；多线程并发调用 {@link #getAccessKey()} 时仅会触发一次刷新。</p>
 */
@Slf4j
public class AccessKeyManager {

    private final PushPlusConfig config;
    private final AccessKeyApi accessKeyApi;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile String cachedKey;
    /** 最早过期时间戳（含提前量），到达此刻必须刷新。 */
    private volatile Instant expireAt = Instant.EPOCH;

    public AccessKeyManager(PushPlusConfig config, AccessKeyApi accessKeyApi) {
        this.config = config;
        this.accessKeyApi = accessKeyApi;
    }

    /**
     * 获取有效的 AccessKey。如已缓存且未过期则直接返回；否则触发刷新。
     */
    public String getAccessKey() {
        if (isValid()) {
            return cachedKey;
        }
        return refresh();
    }

    /**
     * 强制刷新。多线程并发调用时仅会真正发起一次刷新请求。
     */
    public String refresh() {
        lock.lock();
        try {
            if (isValid()) {
                return cachedKey;
            }
            AccessKeyResult result = accessKeyApi.getAccessKey();
            if (result == null || result.getAccessKey() == null) {
                throw new PushPlusException("获取 AccessKey 失败：返回为空");
            }
            cachedKey = result.getAccessKey();
            long ttlSeconds = result.getExpiresIn() == null ? 7200L : result.getExpiresIn();
            long aheadSeconds = Math.max(0, config.getAccessKeyRefreshAheadSeconds());
            long effectiveTtl = Math.max(1, ttlSeconds - aheadSeconds);
            expireAt = Instant.now().plusSeconds(effectiveTtl);
            log.debug("[pushplus] AccessKey refreshed, ttl={}s, refresh in <= {}s", ttlSeconds, effectiveTtl);
            return cachedKey;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 失效缓存。下次调用 {@link #getAccessKey()} 时会重新拉取。
     * 在收到接口返回的“AccessKey 已失效”时手动触发。
     */
    public void invalidate() {
        lock.lock();
        try {
            cachedKey = null;
            expireAt = Instant.EPOCH;
        } finally {
            lock.unlock();
        }
    }

    private boolean isValid() {
        return cachedKey != null && Instant.now().isBefore(expireAt);
    }
}
