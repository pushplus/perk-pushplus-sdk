package com.perk.pushplus.example;

import com.perk.pushplus.callback.CallbackParser;
import com.perk.pushplus.model.callback.CallbackPayload;

/**
 * 回调请求体解析示例：在你的 web 接口里把 raw JSON 传给 {@link CallbackParser}
 * 即可获得强类型的 {@link CallbackPayload}，再按事件类型分发处理。
 */
public class CallbackHandlingDemo {

    public static void main(String[] args) {
        String body = """
                {
                  "event": "message_complate",
                  "messageInfo": {
                    "message": "",
                    "shortCode": "abc123",
                    "sendStatus": 2
                  }
                }
                """;

        CallbackPayload payload = CallbackParser.parse(body);

        switch (payload.getEvent()) {
            case MESSAGE_COMPLETE -> {
                var info = payload.getMessageInfo();
                System.out.printf("消息 %s 状态: %s%n", info.getShortCode(), info.getSendStatusEnum());
            }
            case ADD_TOPIC_USER -> {
                var info = payload.getTopicUserInfo();
                System.out.printf("新成员加入群组 %s: %s%n", info.getTopicCode(), info.getNickName());
            }
            case ADD_FRIEND -> {
                var info = payload.getFriendInfo();
                System.out.printf("新增好友 %s, qrCode=%s%n", info.getNickName(), payload.getQrCode());
            }
            case null -> System.out.println("未知事件");
        }
    }
}
