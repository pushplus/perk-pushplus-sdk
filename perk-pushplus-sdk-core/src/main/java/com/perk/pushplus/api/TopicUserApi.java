package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.topic.TopicUserBlacklistItem;
import com.perk.pushplus.model.open.topic.TopicUserItem;
import com.perk.pushplus.model.open.topic.TopicUserListQuery;

import java.util.Map;

/**
 * 开放接口 - 群组用户（文档「六. 群组用户接口」）。
 */
public class TopicUserApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<TopicUserItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<TopicUserBlacklistItem>>> BLACKLIST =
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

    /**
     * 4. 将订阅人加入黑名单。
     *
     * <p>加入后将移出群组，对方无法再加入该群组。积分群组不支持黑名单。不能将自己加入黑名单。</p>
     *
     * @param topicRelationId 用户编号（订阅人列表中的 id 字段）
     */
    public void addBlacklist(long topicRelationId) {
        var path = appendQuery("/api/open/topicUser/addBlacklist", Map.of("topicRelationId", topicRelationId));
        executeOpen("POST", path, null, ANY);
    }

    /**
     * 5. 订阅人黑名单列表。
     *
     * @param query 需在 params 中携带 topicId，可用 {@link TopicUserListQuery#of(Integer, Integer, long)}
     */
    public PageResult<TopicUserBlacklistItem> blacklistList(TopicUserListQuery query) {
        return executeOpen("POST", "/api/open/topicUser/blacklistList", query, BLACKLIST);
    }

    /**
     * 6. 解除订阅人黑名单。
     *
     * <p>解除后不会自动恢复群组订阅，对方可重新加入该群组。</p>
     *
     * @param id 黑名单记录 ID（黑名单列表中的 id 字段）
     */
    public void removeBlacklist(long id) {
        executeOpen("POST", appendQuery("/api/open/topicUser/removeBlacklist", Map.of("id", id)), null, ANY);
    }
}
