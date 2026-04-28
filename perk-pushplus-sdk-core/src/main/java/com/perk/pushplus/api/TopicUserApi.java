package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.topic.TopicUserItem;
import com.perk.pushplus.model.open.topic.TopicUserListQuery;

import java.util.Map;

/**
 * 开放接口 - 群组用户（文档「六. 群组用户接口」）。
 */
public class TopicUserApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<TopicUserItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public TopicUserApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 获取群组内用户。 */
    public PageResult<TopicUserItem> subscriberList(TopicUserListQuery query) {
        return executeOpen("POST", "/api/open/topicUser/subscriberList", query, LIST);
    }

    /** 2. 删除群组内用户。 */
    public String deleteUser(long topicRelationId) {
        var path = appendQuery("/api/open/topicUser/deleteTopicUser", Map.of("topicRelationId", topicRelationId));
        return executeOpen("POST", path, null, STRING);
    }

    /** 3. 修改订阅人备注。 */
    public void editRemark(long id, String remark) {
        executeOpen("POST", "/api/open/topicUser/editRemark", Map.of("id", id, "remark", remark), ANY);
    }
}
