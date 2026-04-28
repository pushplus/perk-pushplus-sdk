package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.access.AccessKeyManager;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.PageResult;
import io.github.perk.pushplus.model.open.topic.TopicAddRequest;
import io.github.perk.pushplus.model.open.topic.TopicDetail;
import io.github.perk.pushplus.model.open.topic.TopicEditRequest;
import io.github.perk.pushplus.model.open.topic.TopicItem;
import io.github.perk.pushplus.model.open.topic.TopicListQuery;
import io.github.perk.pushplus.model.open.topic.TopicQrCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 开放接口 - 群组接口（文档「五. 群组接口」）。
 */
public class TopicApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<TopicItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<TopicDetail>> DETAIL =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Long>> LONG =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<TopicQrCode>> QR =
            new TypeReference<>() {};

    public TopicApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 群组列表。 */
    public PageResult<TopicItem> list(TopicListQuery query) {
        return executeOpen("POST", "/api/open/topic/list", query == null ? new TopicListQuery() : query, LIST);
    }

    /** 2. 获取我创建的群组详情。 */
    public TopicDetail detail(long topicId) {
        return executeOpen("GET", appendQuery("/api/open/topic/detail", Map.of("topicId", topicId)), null, DETAIL);
    }

    /** 3. 获取我加入的群详情。 */
    public TopicDetail joinDetail(long topicId) {
        return executeOpen("GET", appendQuery("/api/open/topic/joinTopicDetail", Map.of("topicId", topicId)), null, DETAIL);
    }

    /** 4. 新增群组，返回新建群组编号。 */
    public Long add(TopicAddRequest req) {
        return executeOpen("POST", "/api/open/topic/add", req, LONG);
    }

    /** 5. 修改群组。 */
    public String edit(TopicEditRequest req) {
        return executeOpen("POST", "/api/open/topic/editTopic", req, STRING);
    }

    /** 6. 获取群组二维码。 */
    public TopicQrCode qrCode(long topicId, Integer second, Integer scanCount) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("topicId", topicId);
        if (second != null) p.put("second", second);
        if (scanCount != null) p.put("scanCount", scanCount);
        return executeOpen("GET", appendQuery("/api/open/topic/qrCode", p), null, QR);
    }

    /** 7. 退出群组。 */
    public String exit(long topicId) {
        return executeOpen("GET", appendQuery("/api/open/topic/exitTopic", Map.of("topicId", topicId)), null, STRING);
    }

    /** 8. 删除群组。 */
    public String delete(long topicId) {
        return executeOpen("GET", appendQuery("/api/open/topic/delete", Map.of("topicId", topicId)), null, STRING);
    }

    /**
     * 9. 上下架积分群组。
     *
     * @param isOpen 1-上架，0-下架
     */
    public String setOpen(long topicId, int isOpen) {
        Map<String, Object> body = Map.of("topic", topicId, "isOpen", isOpen);
        return executeOpen("POST", "/api/open/topic/isOpen", body, STRING);
    }
}
