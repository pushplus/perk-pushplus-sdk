package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.exception.PushPlusException;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.send.BatchSendRequest;
import io.github.perk.pushplus.model.send.BatchSendResult;
import io.github.perk.pushplus.model.send.SendRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 发送消息接口。
 *
 * <p>对应 PushPlus 文档「二. 发送消息接口」与「三. 多渠道发送消息接口」。</p>
 */
public class MessageApi extends AbstractApi {

    private static final TypeReference<ApiResponse<String>> SEND_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<List<BatchSendResult>>> BATCH_RESP =
            new TypeReference<>() {};

    public MessageApi(PushPlusConfig config, HttpRequester http) {
        super(config, http);
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
        return executeForData("POST", "/send", null, req, SEND_RESP);
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
        return executeForData("POST", "/batchSend", null, req, BATCH_RESP);
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
