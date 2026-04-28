package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.ErrorCode;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.send.BatchSendRequest;
import com.perk.pushplus.model.send.BatchSendResult;
import com.perk.pushplus.model.send.SendRequest;
import com.perk.pushplus.ratelimit.RateLimitGuard;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 发送消息接口。
 *
 * <p>对应 PushPlus 文档「二. 发送消息接口」与「三. 多渠道发送消息接口」。</p>
 *
 * <p>内置本地限流守卫：当上游返回 {@link ErrorCode#RATE_LIMITED}（code=900，请求次数过多）时，
 * 后续对同一 token 的发送调用会在 SDK 内被直接短路抛 {@link PushPlusException}，
 * 不再发起 HTTP，直到守卫到期自动解除。
 * 该行为可通过 {@link PushPlusConfig#isRateLimitGuardEnabled()} 关闭。</p>
 */
public class MessageApi extends AbstractApi {

    private static final TypeReference<ApiResponse<String>> SEND_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<List<BatchSendResult>>> BATCH_RESP =
            new TypeReference<>() {};

    private final RateLimitGuard rateLimitGuard;

    public MessageApi(PushPlusConfig config, HttpRequester http) {
        this(config, http, new RateLimitGuard(config));
    }

    public MessageApi(PushPlusConfig config, HttpRequester http, RateLimitGuard rateLimitGuard) {
        super(config, http);
        this.rateLimitGuard = rateLimitGuard != null ? rateLimitGuard : new RateLimitGuard(config);
    }

    /** 暴露限流守卫，便于运维场景手动 clear 或观察解禁时间。 */
    public RateLimitGuard getRateLimitGuard() {
        return rateLimitGuard;
    }

    /**
     * 发送单条消息（同步）。
     *
     * @param request 请求体；token 可省略，SDK 会自动注入
     * @return 消息流水号
     */
    public String send(SendRequest request) {
        SendRequest req = withDefaultToken(request);
        validateSend(req);
        rateLimitGuard.check(req.getToken());
        return executeWithGuard(req.getToken(),
                () -> executeForData("POST", "/send", null, req, SEND_RESP));
    }

    /**
     * 发送单条消息（异步）。
     */
    public CompletableFuture<String> sendAsync(SendRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request));
    }

    /**
     * 多渠道发送消息。
     *
     * @return 每个渠道一个 {@link BatchSendResult}
     */
    public List<BatchSendResult> batchSend(BatchSendRequest request) {
        BatchSendRequest req = withDefaultToken(request);
        validateBatch(req);
        rateLimitGuard.check(req.getToken());
        return executeWithGuard(req.getToken(),
                () -> executeForData("POST", "/batchSend", null, req, BATCH_RESP));
    }

    /**
     * 多渠道发送消息（异步）。
     */
    public CompletableFuture<List<BatchSendResult>> batchSendAsync(BatchSendRequest request) {
        return CompletableFuture.supplyAsync(() -> batchSend(request));
    }

    /**
     * 便捷方法：以默认渠道、默认模板发送一条简单消息。
     *
     * @return 消息流水号
     */
    public String sendSimple(String title, String content) {
        return send(SendRequest.builder().title(title).content(content).build());
    }

    /* ============================== 内部辅助 ============================== */

    /**
     * 在执行真实请求时拦截 code=900 的业务异常：登记到本地限流守卫，再原样抛出。
     * 其它异常直接透传，行为保持与原来一致。
     */
    private <T> T executeWithGuard(String token, java.util.function.Supplier<T> call) {
        try {
            return call.get();
        } catch (PushPlusException e) {
            if (e.isRateLimited()) {
                rateLimitGuard.markBlocked(token);
            }
            throw e;
        }
    }

    private SendRequest withDefaultToken(SendRequest req) {
        if (req == null) {
            throw new PushPlusException("SendRequest 不能为空");
        }
        if (req.getToken() == null || req.getToken().isBlank()) {
            return req.toBuilder().token(requireToken()).build();
        }
        return req;
    }

    private BatchSendRequest withDefaultToken(BatchSendRequest req) {
        if (req == null) {
            throw new PushPlusException("BatchSendRequest 不能为空");
        }
        if (req.getToken() == null || req.getToken().isBlank()) {
            return req.toBuilder().token(requireToken()).build();
        }
        return req;
    }

    private String requireToken() {
        String t = config.getToken();
        if (t == null || t.isBlank()) {
            throw new PushPlusException("发送消息需要 token，但 PushPlusConfig.token 为空");
        }
        return t;
    }

    private static void validateSend(SendRequest req) {
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new PushPlusException("发送消息 content 不能为空");
        }
    }

    private static void validateBatch(BatchSendRequest req) {
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new PushPlusException("批量发送消息 content 不能为空");
        }
    }
}
