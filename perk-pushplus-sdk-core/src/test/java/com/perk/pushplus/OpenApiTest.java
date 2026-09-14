package com.perk.pushplus;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.enums.SendStatus;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.message.MessageItem;
import com.perk.pushplus.model.open.message.SendMessageResult;
import com.perk.pushplus.model.open.topic.TopicListQuery;
import com.perk.pushplus.model.open.topic.TopicUserListQuery;
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

    @Test
    void form_create_save_publish() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/push/api/open/form/create", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"id\":10001,\"title\":\"用户满意度调查\",\"status\":0}}")
                .whenPath("/push/api/open/form/save", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/push/api/open/form/publish", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"id\":10001,\"formCode\":\"a1b2c3d4\",\"status\":1}}");

        PushPlusClient c = client(http);
        var created = c.getForm().create("用户满意度调查");
        assertEquals(10001L, created.getId());
        c.getForm().save(com.perk.pushplus.model.open.form.FormSaveRequest.builder()
                .id(10001L)
                .title("用户满意度调查")
                .items(java.util.List.of(java.util.Map.of("id", "q1", "type", "input", "label", "姓名")))
                .build());
        var published = c.getForm().publish(10001L);
        assertEquals("a1b2c3d4", published.getFormCode());

        var saveReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/form/save"))
                .findFirst().orElseThrow();
        assertTrue(saveReq.body().contains("\"q1\""), saveReq.body());
        var publishReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/form/publish"))
                .findFirst().orElseThrow();
        assertTrue(publishReq.url().contains("id=10001"), publishReq.url());
    }

    @Test
    void excel_save_content_serializes_object() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/push/api/open/excel/saveContent", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"docCode\":\"Sh3xY7kP\",\"publishDirty\":true}}");

        PushPlusClient c = client(http);
        var vo = c.getExcel().saveContent("Sh3xY7kP", java.util.Map.of("sheetOrder", java.util.List.of("sheet-1")));
        assertEquals("Sh3xY7kP", vo.getDocCode());

        var saveReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/excel/saveContent"))
                .findFirst().orElseThrow();
        assertTrue(saveReq.body().contains("sheetOrder"), saveReq.body());
        assertTrue(saveReq.body().contains("\\\"sheet-1\\\"") || saveReq.body().contains("sheet-1"),
                saveReq.body());
    }

    @Test
    void friend_and_topic_user_blacklist() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/friend/addBlacklist", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/friend/blacklistList", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":1,\"pages\":1,\"list\":[" +
                                "{\"id\":4,\"friendId\":1322,\"nickName\":\"昵称\",\"createTime\":\"2026-08-17 10:00:00\"}]}}")
                .whenPath("/api/open/friend/removeBlacklist", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/topicUser/addBlacklist", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/topicUser/blacklistList", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":1,\"pages\":1,\"list\":[" +
                                "{\"id\":1,\"userId\":1322,\"nickName\":\"昵称\",\"openId\":\"o0a\"}]}}")
                .whenPath("/api/open/topicUser/removeBlacklist", 200, "{\"code\":200,\"msg\":\"ok\"}");

        PushPlusClient c = client(http);
        c.getFriend().addBlacklist(1322L);
        var friends = c.getFriend().blacklistList(PageQuery.of(1, 20));
        assertEquals(4L, friends.getList().get(0).getId());
        assertEquals(1322L, friends.getList().get(0).getFriendId());
        c.getFriend().removeBlacklist(4L);

        c.getTopicUser().addBlacklist(10L);
        var users = c.getTopicUser().blacklistList(TopicUserListQuery.of(1, 20, 100L));
        assertEquals(1L, users.getList().get(0).getId());
        c.getTopicUser().removeBlacklist(1L);

        var friendAdd = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/friend/addBlacklist"))
                .findFirst().orElseThrow();
        assertTrue(friendAdd.url().contains("friendId=1322"), friendAdd.url());
        var topicAdd = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/topicUser/addBlacklist"))
                .findFirst().orElseThrow();
        assertTrue(topicAdd.url().contains("topicRelationId=10"), topicAdd.url());
        var topicList = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/topicUser/blacklistList"))
                .findFirst().orElseThrow();
        assertTrue(topicList.body().contains("\"topicId\":100"), topicList.body());
    }

    @Test
    void form_list_uses_current_and_params() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/push/api/open/form/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":0,\"pages\":0,\"list\":[]}}");

        client(http).getForm().list(com.perk.pushplus.model.open.form.FormListQuery.of(1, 20, "满意度", 1));

        var listReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/form/list"))
                .findFirst().orElseThrow();
        assertTrue(listReq.body().contains("\"current\":1"), listReq.body());
        assertTrue(listReq.body().contains("\"keyword\":\"满意度\""), listReq.body());
        assertTrue(listReq.body().contains("\"status\":1"), listReq.body());
    }

    @Test
    void doc_import() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/push/api/open/doc/import", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"docCode\":\"Ab3xY7kP\",\"title\":\"本周工作同步\"}}");

        PushPlusClient c = client(http);
        var imported = c.getDoc().importWord("hello".getBytes(java.nio.charset.StandardCharsets.UTF_8), "本周工作同步.docx");
        assertEquals("Ab3xY7kP", imported.getDocCode());

        var importReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/doc/import"))
                .findFirst().orElseThrow();
        assertTrue(importReq.headers().get("Content-Type").startsWith("multipart/form-data; boundary="),
                importReq.headers().get("Content-Type"));
        assertTrue(importReq.body().contains("filename=\"本周工作同步.docx\""), importReq.body());
    }

    @Test
    void excel_import() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/push/api/open/excel/import", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"docCode\":\"Sh3xY7kP\",\"title\":\"销售日报\"}}");

        PushPlusClient c = client(http);
        var imported = c.getExcel().importExcel("xlsx".getBytes(java.nio.charset.StandardCharsets.UTF_8), "销售日报.xlsx");
        assertEquals("Sh3xY7kP", imported.getDocCode());

        var importReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/push/api/open/excel/import"))
                .findFirst().orElseThrow();
        assertTrue(importReq.headers().get("Content-Type").startsWith("multipart/form-data"),
                String.valueOf(importReq.headers().get("Content-Type")));
    }

    @Test
    void cmcc_bind_and_status() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/cmcc/bind", 200, "{\"code\":200,\"msg\":\"绑定成功\"}")
                .whenPath("/api/open/cmcc/info", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"bound\":1,\"apiKeyMasked\":\"ak_***xxx\"," +
                                "\"createTime\":\"2026-09-14 10:20:00\"}}")
                .whenPath("/api/open/cmcc/test", 200, "{\"code\":200,\"msg\":\"测试消息已发送\"}")
                .whenPath("/api/open/cmcc/unbind", 200, "{\"code\":200,\"msg\":\"ok\"}");

        PushPlusClient c = client(http);

        c.getCmcc().bind("ak_xxxxxxxxxxxxxxxx");
        var bindReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/cmcc/bind"))
                .findFirst().orElseThrow();
        assertTrue(bindReq.body().contains("\"apiKey\":\"ak_xxxxxxxxxxxxxxxx\""), bindReq.body());

        var info = c.getCmcc().info();
        assertEquals(1, info.getBound());
        assertEquals("ak_***xxx", info.getApiKeyMasked());

        c.getCmcc().sendTest();
        assertTrue(http.getRecords().stream().anyMatch(r -> r.url().contains("/api/open/cmcc/test")));

        c.getCmcc().unbind();
        assertTrue(http.getRecords().stream().anyMatch(r -> r.url().contains("/api/open/cmcc/unbind")));
    }

    @Test
    void qq_bot_bind_and_group_config() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/qqBot/getBindLink", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"url\":\"https://qun.qq.com/qunpro/robot/share?robot_appid=1\"," +
                                "\"bindCode\":\"A1B2C3\",\"expireSeconds\":300,\"botName\":\"pushplus\"}}")
                .whenPath("/api/open/qqBot/botInfo", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"isBind\":1,\"receiveStatus\":1," +
                                "\"botInfo\":{\"appId\":\"1\",\"username\":\"pushplus\"}}}")
                .whenPath("/api/open/qqBot/groupList", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":[{\"id\":9,\"groupOpenId\":\"OPEN-1\",\"status\":1," +
                                "\"groupName\":\"运维告警群\",\"groupTags\":[\"运维\"],\"groupMemberNum\":128}]}")
                .whenPath("/api/open/qqBot/add", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/qqBot/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"pageSize\":20,\"total\":1,\"pages\":1,\"list\":[" +
                                "{\"id\":3,\"qqName\":\"运维告警群\",\"qqCode\":\"ops-group\",\"sendType\":2,\"qqGroupId\":9}]}}")
                .whenPath("/api/open/qqBot/delete", 200, "{\"code\":200,\"msg\":\"ok\"}");

        PushPlusClient c = client(http);

        var link = c.getQqBot().getBindLink(true);
        assertEquals("A1B2C3", link.getBindCode());
        assertEquals(300, link.getExpireSeconds());
        var linkReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/qqBot/getBindLink"))
                .findFirst().orElseThrow();
        assertTrue(linkReq.url().contains("refresh=true"), linkReq.url());

        var bind = c.getQqBot().botInfo();
        assertEquals(1, bind.getIsBind());
        assertEquals("pushplus", bind.getBotInfo().getUsername());

        var groups = c.getQqBot().groupList();
        assertEquals(9L, groups.get(0).getId());
        assertEquals(java.util.List.of("运维"), groups.get(0).getGroupTags());

        c.getQqBot().add(com.perk.pushplus.model.open.qq.QqBotSaveRequest.builder()
                .qqName("运维告警群")
                .qqCode("ops-group")
                .qqGroupId(9L)
                .build());
        var addReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/qqBot/add"))
                .findFirst().orElseThrow();
        assertTrue(addReq.body().contains("\"sendType\":2"), addReq.body());
        assertTrue(addReq.body().contains("\"qqGroupId\":9"), addReq.body());

        var page = c.getQqBot().list(PageQuery.of(1, 20));
        assertEquals("ops-group", page.getList().get(0).getQqCode());

        c.getQqBot().delete(3L);
        var deleteReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/qqBot/delete"))
                .findFirst().orElseThrow();
        assertEquals("DELETE", deleteReq.method());
        assertTrue(deleteReq.url().contains("id=3"), deleteReq.url());
    }

    @Test
    void forward_rule_and_log() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/forwardRule/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"list\":[{\"id\":1,\"ruleName\":\"阿里云监控多渠道\",\"sourceType\":1}]}}")
                .whenPath("/api/open/forwardRule/add", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/forwardRule/test", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"matched\":true,\"title\":\"ECS-内存使用率\"}}")
                .whenPath("/api/open/forwardRule/setting?mode=1", 200, "{\"code\":200,\"msg\":\"ok\"}")
                .whenPath("/api/open/forwardRule/setting", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"mode\":1}}")
                .whenPath("/api/open/forwardLog/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"pageNum\":1,\"list\":[{\"id\":9,\"ruleId\":1,\"matchResult\":1}]}}")
                .whenPath("/api/open/forwardLog/detail", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"id\":9,\"ruleId\":1,\"requestBody\":\"{\\\"alertState\\\":\\\"ALERT\\\"}\"}}");

        PushPlusClient c = client(http);
        var page = c.getForwardRule().list(PageQuery.of(1, 20));
        assertEquals("阿里云监控多渠道", page.getList().get(0).getRuleName());

        c.getForwardRule().add(com.perk.pushplus.model.open.forward.ForwardRuleSaveRequest.builder()
                .ruleName("阿里云监控多渠道")
                .tokenId(-1L)
                .sourceType(1)
                .variables(java.util.List.of(com.perk.pushplus.model.open.forward.ForwardVariable.builder()
                        .varName("alertState")
                        .sourceType(3)
                        .extractType(1)
                        .extractKey("alertState")
                        .build()))
                .build());
        var tested = c.getForwardRule().test(com.perk.pushplus.model.open.forward.ForwardRuleTestRequest.builder()
                .sourceType(1)
                .body("{\"alertState\":\"ALERT\"}")
                .conditionExpr("alertState == 'ALERT'")
                .build());
        assertEquals(Boolean.TRUE, tested.getMatched());
        c.getForwardRule().saveSetting(1);
        assertEquals(1, c.getForwardRule().getSetting().getMode());

        var logs = c.getForwardLog().list(
                com.perk.pushplus.model.open.forward.ForwardLogListQuery.of(1, 20, 1L, 1));
        assertEquals(9L, logs.getList().get(0).getId());
        assertTrue(c.getForwardLog().detail(9L).getRequestBody().contains("ALERT"));

        var addReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/forwardRule/add"))
                .findFirst().orElseThrow();
        assertTrue(addReq.body().contains("\"tokenId\":-1"), addReq.body());
        var saveSetting = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/forwardRule/setting?mode=1"))
                .findFirst().orElseThrow();
        assertEquals("GET", saveSetting.method());
        var logList = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/forwardLog/list"))
                .findFirst().orElseThrow();
        assertTrue(logList.body().contains("\"matchResult\":1"), logList.body());
        var logDetail = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/forwardLog/detail"))
                .findFirst().orElseThrow();
        assertTrue(logDetail.url().contains("logId=9"), logDetail.url());
    }
}

