package com.perk.pushplus;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.Channel;
import com.perk.pushplus.enums.Template;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.model.send.BatchSendRequest;
import com.perk.pushplus.model.send.BatchSendResult;
import com.perk.pushplus.model.send.SendRequest;
import com.perk.pushplus.test.MockHttpRequester;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageApiTest {

    private PushPlusClient newClient(MockHttpRequester http) {
        return PushPlusClient.builder()
                .config(PushPlusConfig.builder().token("user_token").secretKey("sk").build())
                .httpRequester(http)
                .build();
    }

    @Test
    void send_should_inject_token_and_return_short_code() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/send", 200,
                        "{\"code\":200,\"msg\":\"请求成功\",\"data\":\"abc123\"}");

        PushPlusClient client = newClient(http);
        String shortCode = client.sendSimple("hi", "world");

        assertEquals("abc123", shortCode);
        var rec = http.getRecords().get(0);
        assertEquals("POST", rec.method());
        assertTrue(rec.url().endsWith("/send"));
        assertTrue(rec.body().contains("\"token\":\"user_token\""), "请求体应自动注入 token");
        assertTrue(rec.body().contains("\"content\":\"world\""));
    }

    @Test
    void send_should_throw_when_content_blank() {
        PushPlusClient client = newClient(new MockHttpRequester());
        assertThrows(PushPlusException.class,
                () -> client.send(SendRequest.builder().title("only title").build()));
    }

    @Test
    void send_should_propagate_business_failure() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/send", 200,
                        "{\"code\":903,\"msg\":\"内容不合法\"}");
        PushPlusClient client = newClient(http);
        PushPlusException ex = assertThrows(PushPlusException.class,
                () -> client.sendSimple("t", "c"));
        assertEquals(903, ex.getCode());
    }

    @Test
    void batch_send_builder_should_join_csv_in_order() {
        BatchSendRequest req = BatchSendRequest.builder()
                .title("multi")
                .content("hello")
                .channel(Channel.WECHAT).option("")
                .channel(Channel.WEBHOOK).option("bark")
                .channel(Channel.EXTENSION).option("")
                .template(Template.MARKDOWN)
                .build();

        assertEquals("wechat,webhook,extension", req.getChannel());
        assertEquals(",bark,", req.getOption());
    }

    @Test
    void batch_send_should_parse_response_list() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/batchSend", 200,
                        "{\"code\":200,\"msg\":\"执行成功\",\"data\":[" +
                                "{\"shortCode\":\"a\",\"message\":\"ok\",\"code\":200,\"channel\":\"wechat\"}," +
                                "{\"shortCode\":\"b\",\"message\":\"ok\",\"code\":200,\"channel\":\"webhook\"}]}" );
        PushPlusClient client = newClient(http);

        List<BatchSendResult> list = client.batchSend(BatchSendRequest.builder()
                .content("c")
                .channel(Channel.WECHAT).option("")
                .channel(Channel.WEBHOOK).option("bark")
                .build());

        assertEquals(2, list.size());
        assertEquals(Channel.WECHAT, list.get(0).getChannel());
        assertEquals("b", list.get(1).getShortCode());
    }
}
