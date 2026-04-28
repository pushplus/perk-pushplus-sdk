package io.github.perk.pushplus;

import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.test.MockHttpRequester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessKeyManagerTest {

    @Test
    void should_cache_access_key_and_only_call_once() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/api/common/openApi/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK1\",\"expiresIn\":7200}}")
                .whenPath("/api/open/user/token", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":\"USER_TOKEN\"}")
                .whenPath("/api/open/user/myInfo", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"nickName\":\"u\",\"token\":\"t\"}}");

        PushPlusClient client = PushPlusClient.builder()
                .config(PushPlusConfig.builder().token("u").secretKey("s").build())
                .httpRequester(http)
                .build();

        String t1 = client.getUser().getToken();
        var info = client.getUser().myInfo();

        assertEquals("USER_TOKEN", t1);
        assertEquals("u", info.getNickName());

        long accessCalls = http.getRecords().stream()
                .filter(r -> r.url().contains("/getAccessKey"))
                .count();
        assertEquals(1, accessCalls, "AccessKey 应只请求一次（缓存命中）");

        boolean allHaveHeader = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/"))
                .allMatch(r -> "AK1".equals(r.headers().get("access-key")));
        assertTrue(allHaveHeader, "所有开放接口都应携带 access-key header");
    }

    @Test
    void should_refresh_when_access_key_invalid() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/api/common/openApi/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK_OLD\",\"expiresIn\":7200}}")
                .whenPath("/api/common/openApi/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK_NEW\",\"expiresIn\":7200}}")
                .whenPath("/api/open/user/token", 200,
                        "{\"code\":401,\"msg\":\"AccessKey 失效\"}")
                .whenPath("/api/open/user/token", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":\"OK\"}");

        PushPlusClient client = PushPlusClient.builder()
                .config(PushPlusConfig.builder().token("u").secretKey("s").build())
                .httpRequester(http)
                .build();

        String t = client.getUser().getToken();
        assertEquals("OK", t);

        long accessCalls = http.getRecords().stream()
                .filter(r -> r.url().contains("/getAccessKey"))
                .count();
        assertEquals(2, accessCalls, "401 后应触发 AccessKey 刷新");
    }
}
