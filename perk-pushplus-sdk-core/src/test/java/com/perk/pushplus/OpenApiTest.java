package com.perk.pushplus;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.SendStatus;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.message.MessageItem;
import com.perk.pushplus.model.open.message.SendMessageResult;
import com.perk.pushplus.model.open.topic.TopicListQuery;
import com.perk.pushplus.test.MockHttpRequester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiTest {

    private PushPlusClient client(MockHttpRequester http) {
        return PushPlusClient.builder()
                .config(PushPlusConfig.builder().token("u").secretKey("s").build())
                .httpRequester(http)
                .build();
    }

    @Test
    void open_message_list_and_query_result() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/message/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":1,\"pages\":1,\"list\":[" +
                                "{\"shortCode\":\"sc1\",\"title\":\"hi\",\"channel\":\"wechat\",\"messageType\":1,\"updateTime\":\"2024-01-01\"}]}}")
                .whenPath("/api/open/message/sendMessageResult", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"status\":2,\"errorMessage\":\"\",\"updateTime\":\"2024-01-01\"}}");

        PushPlusClient c = client(http);
        PageResult<MessageItem> page = c.getOpenMessage().list(PageQuery.of(1, 20));
        assertEquals(1, page.getTotal());
        assertEquals("sc1", page.getList().get(0).getShortCode());

        SendMessageResult r = c.getOpenMessage().queryResult("sc1");
        assertEquals(SendStatus.SUCCESS, r.getStatusEnum());
    }

    @Test
    void topic_list_should_serialize_params() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/topic/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":0,\"pages\":0,\"list\":[]}}");

        client(http).getTopic().list(TopicListQuery.of(1, 20, 0));

        var topicReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/topic/list"))
                .findFirst().orElseThrow();
        assertTrue(topicReq.body().contains("\"topicType\":0"),
                "topicType 应在 params 中: " + topicReq.body());
    }

    @Test
    void topic_detail_business_error_should_not_throw_jackson_exception() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                // 业务失败时服务端把错误描述放在 data（字符串），而非业务对象。
                .whenPath("/api/open/topic/detail", 200,
                        "{\"code\":999,\"msg\":\"服务端验证错误\",\"data\":\"群组不存在\"}");

        PushPlusException ex = assertThrows(PushPlusException.class,
                () -> client(http).getTopic().detail(12L));

        assertEquals(999, ex.getCode(), "应保留服务端业务码");
        assertTrue(ex.getMessage().contains("群组不存在"),
                "异常信息应包含 data 中的错误描述: " + ex.getMessage());
        assertFalse(ex.getMessage().contains("Cannot construct instance"),
                "不应再出现 Jackson 反序列化错误: " + ex.getMessage());
    }
}

