package com.perk.pushplus.callback;

import com.perk.pushplus.json.JsonMapper;
import com.perk.pushplus.model.callback.CallbackPayload;

/**
 * PushPlus 回调请求体解析工具。
 *
 * <p>用法：在你的回调接口中拿到原始 JSON body，直接传入 {@link #parse(String)} 即可。</p>
 *
 * <pre>{@code
 * @PostMapping("/pushplus/callback")
 * public String callback(@RequestBody String body) {
 *     CallbackPayload payload = CallbackParser.parse(body);
 *     switch (payload.getEvent()) {
 *         case MESSAGE_COMPLETE -> handleMessage(payload.getMessageInfo());
 *         case ADD_TOPIC_USER -> handleNewSubscriber(payload.getTopicUserInfo());
 *         case ADD_FRIEND -> handleNewFriend(payload.getFriendInfo(), payload.getQrCode());
 *     }
 *     return "ok";
 * }
 * }</pre>
 */
public final class CallbackParser {

    private CallbackParser() {
    }

    public static CallbackPayload parse(String json) {
        return JsonMapper.fromJson(json, CallbackPayload.class);
    }
}
