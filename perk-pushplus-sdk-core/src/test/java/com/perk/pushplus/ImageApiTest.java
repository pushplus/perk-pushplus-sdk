package com.perk.pushplus;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.image.ImageItem;
import com.perk.pushplus.model.open.image.ImageUploadResult;
import com.perk.pushplus.model.open.image.ImageUploadToken;
import com.perk.pushplus.test.MockHttpRequester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageApiTest {

    private PushPlusClient client(MockHttpRequester http) {
        return PushPlusClient.builder()
                .config(PushPlusConfig.builder().token("u").secretKey("s").build())
                .httpRequester(http)
                .build();
    }

    @Test
    void get_upload_token_should_return_qiniu_info() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/userImage/uploadToken", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{" +
                                "\"uploadToken\":\"qiniu-token\"," +
                                "\"uploadHost\":\"https://upload.qiniup.com\"," +
                                "\"uploadUrl\":\"https://upload.qiniup.com/\"," +
                                "\"bucket\":\"pushplus-img\"," +
                                "\"expiresIn\":600}}");

        ImageUploadToken t = client(http).getImage().getUploadToken();
        assertNotNull(t);
        assertEquals("qiniu-token", t.getUploadToken());
        assertEquals("https://upload.qiniup.com/", t.getUploadUrl());
        assertEquals("pushplus-img", t.getBucket());
        assertEquals(600, t.getExpiresIn());
    }

    @Test
    void upload_should_post_multipart_to_qiniu_without_access_key() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/userImage/uploadToken", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{" +
                                "\"uploadToken\":\"qiniu-token\"," +
                                "\"uploadHost\":\"https://upload.qiniup.com\"," +
                                "\"uploadUrl\":\"https://upload.qiniup.com/\"," +
                                "\"bucket\":\"pushplus-img\"," +
                                "\"expiresIn\":600}}")
                .whenPath("upload.qiniup.com", 200,
                        "{\"errno\":0,\"ext\":\".png\",\"fname\":\"a.png\"," +
                                "\"fsize\":3,\"hash\":\"H\",\"key\":\"1/H.png\"," +
                                "\"mimeType\":\"image/png\",\"msg\":\"ok\"," +
                                "\"thumbnail\":\"https://pic.pushplus.plus/1/H.png@s\"," +
                                "\"url\":\"https://pic.pushplus.plus/1/H.png@p\"}");

        ImageUploadResult r = client(http).getImage().uploadBytes(new byte[]{1, 2, 3}, "a.png");

        assertTrue(r.isSuccess());
        assertEquals("https://pic.pushplus.plus/1/H.png@p", r.getUrl());
        assertEquals(".png", r.getExt());

        var uploadReq = http.getRecords().stream()
                .filter(rec -> rec.url().contains("upload.qiniup.com"))
                .findFirst().orElseThrow();
        assertEquals("POST", uploadReq.method());
        assertFalse(uploadReq.headers().containsKey("access-key"),
                "上传到七牛云的请求不应携带 access-key");
        String ct = uploadReq.headers().get("Content-Type");
        assertNotNull(ct, "应设置 Content-Type");
        assertTrue(ct.startsWith("multipart/form-data; boundary="),
                "应使用 multipart/form-data: " + ct);
        assertNotNull(uploadReq.body(), "multipart body 不应为空");
        assertTrue(uploadReq.body().contains("name=\"token\""),
                "multipart body 应包含 token 表单字段");
        assertTrue(uploadReq.body().contains("qiniu-token"),
                "multipart body 应包含 uploadToken 值");
        assertTrue(uploadReq.body().contains("filename=\"a.png\""),
                "multipart body 应包含文件名");
    }

    @Test
    void upload_should_throw_when_qiniu_returns_error() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/userImage/uploadToken", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{" +
                                "\"uploadToken\":\"qiniu-token\"," +
                                "\"uploadUrl\":\"https://upload.qiniup.com/\"}}")
                .whenPath("upload.qiniup.com", 200,
                        "{\"errno\":401,\"msg\":\"bad token\"}");

        PushPlusException ex = assertThrows(PushPlusException.class,
                () -> client(http).getImage().uploadBytes(new byte[]{1}, "a.png"));
        assertEquals(401, ex.getCode());
        assertTrue(ex.getMessage().contains("bad token"));
    }

    @Test
    void list_and_delete_should_call_open_api_with_access_key() {
        MockHttpRequester http = new MockHttpRequester()
                .whenPath("/getAccessKey", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{\"accessKey\":\"AK\",\"expiresIn\":7200}}")
                .whenPath("/api/open/userImage/list", 200,
                        "{\"code\":200,\"msg\":\"ok\",\"data\":{" +
                                "\"pageNum\":1,\"pageSize\":10,\"total\":1,\"pages\":1," +
                                "\"list\":[{\"id\":1," +
                                "\"imgUrl\":\"https://pic.pushplus.plus/x.png@p\"," +
                                "\"thumbnail\":\"https://pic.pushplus.plus/x.png@s\"," +
                                "\"createTime\":\"2026-05-09 14:44:40\"}]}}")
                .whenPath("/api/open/userImage/delete", 200,
                        "{\"code\":200,\"msg\":\"执行成功\"}");

        PushPlusClient c = client(http);
        PageResult<ImageItem> page = c.getImage().list(PageQuery.of(1, 10));
        assertEquals(1, page.getTotal());
        assertEquals(1L, page.getList().get(0).getId());

        c.getImage().delete(1L);

        var deleteReq = http.getRecords().stream()
                .filter(r -> r.url().contains("/api/open/userImage/delete"))
                .findFirst().orElseThrow();
        assertEquals("DELETE", deleteReq.method());
        assertTrue(deleteReq.url().contains("id=1"),
                "删除请求应通过 url 传 id 参数: " + deleteReq.url());
        assertEquals("AK", deleteReq.headers().get("access-key"),
                "删除请求应携带 access-key");
    }
}
