package io.github.perk.pushplus;

import io.github.perk.pushplus.callback.CallbackParser;
import io.github.perk.pushplus.enums.CallbackEvent;
import io.github.perk.pushplus.enums.SendStatus;
import io.github.perk.pushplus.model.callback.CallbackPayload;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallbackParserTest {

    @Test
    void parse_message_complete() {
        String json = """
                {
                  "event": "message_complate",
                  "messageInfo": {
                    "message": "",
                    "shortCode": "abc",
                    "sendStatus": 2
                  }
                }
                """;
        CallbackPayload p = CallbackParser.parse(json);
        assertEquals(CallbackEvent.MESSAGE_COMPLETE, p.getEvent());
        assertEquals("abc", p.getMessageInfo().getShortCode());
        assertEquals(SendStatus.SUCCESS, p.getMessageInfo().getSendStatusEnum());
    }

    @Test
    void parse_add_friend_with_qr_code() {
        String json = """
                {
                  "event": "add_friend",
                  "qrCode": "from_landing_page",
                  "friendInfo": {
                    "token": "ftok",
                    "friendId": 5,
                    "nickName": "name"
                  }
                }
                """;
        CallbackPayload p = CallbackParser.parse(json);
        assertEquals(CallbackEvent.ADD_FRIEND, p.getEvent());
        assertEquals("from_landing_page", p.getQrCode());
        assertEquals("ftok", p.getFriendInfo().getToken());
    }

    @Test
    void parse_add_topic_user() {
        String json = """
                {
                  "event": "add_topic_user",
                  "topicUserInfo": {
                    "id": 25,
                    "openId": "ox",
                    "topicId": 2,
                    "nickName": "u"
                  }
                }
                """;
        CallbackPayload p = CallbackParser.parse(json);
        assertEquals(CallbackEvent.ADD_TOPIC_USER, p.getEvent());
        assertEquals(25, p.getTopicUserInfo().getId());
    }
}
