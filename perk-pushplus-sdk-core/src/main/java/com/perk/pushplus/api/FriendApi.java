package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.friend.FriendBlacklistItem;
import com.perk.pushplus.model.open.friend.FriendItem;
import com.perk.pushplus.model.open.friend.FriendQrCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 开放接口 - 好友功能（文档「十. 好友功能接口」）。
 */
public class FriendApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<FriendQrCode>> QR =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<FriendItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<FriendBlacklistItem>>> BLACKLIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public FriendApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 获取个人二维码。 */
    public FriendQrCode getQrCode(String appId, String content, Integer second, Integer scanCount) {
        Map<String, Object> p = new LinkedHashMap<>();
        if (appId != null) p.put("appId", appId);
        if (content != null) p.put("content", content);
        if (second != null) p.put("second", second);
        if (scanCount != null) p.put("scanCount", scanCount);
        return executeOpen("GET", appendQuery("/api/open/friend/getQrCode", p), null, QR);
    }

    /** 2. 获取好友列表。 */
    public PageResult<FriendItem> list(PageQuery query) {
        return executeOpen("POST", "/api/open/friend/list", query == null ? new PageQuery() : query, LIST);
    }

    /** 3. 删除好友。 */
    public void delete(long friendId) {
        executeOpen("GET", appendQuery("/api/open/friend/deleteFriend", Map.of("friendId", friendId)), null, ANY);
    }

    /** 4. 修改好友备注。 */
    public void editRemark(long id, String remark) {
        executeOpen("POST", "/api/open/friend/editRemark", Map.of("id", id, "remark", remark), ANY);
    }

    /**
     * 5. 将好友加入黑名单。
     *
     * <p>加入后将解除双方好友关系，对方无法再添加你。不能将自己加入黑名单，仅可将已有好友加入黑名单。</p>
     *
     * @param friendId 好友 id（好友列表中的 friendId 字段）
     */
    public void addBlacklist(long friendId) {
        executeOpen("POST", appendQuery("/api/open/friend/addBlacklist", Map.of("friendId", friendId)), null, ANY);
    }

    /** 6. 好友黑名单列表。 */
    public PageResult<FriendBlacklistItem> blacklistList(PageQuery query) {
        return executeOpen("POST", "/api/open/friend/blacklistList",
                query == null ? new PageQuery() : query, BLACKLIST);
    }

    /**
     * 7. 解除好友黑名单。
     *
     * <p>解除后不会自动恢复好友关系，需重新扫码添加。</p>
     *
     * @param id 黑名单记录 ID（黑名单列表中的 id 字段）
     */
    public void removeBlacklist(long id) {
        executeOpen("POST", appendQuery("/api/open/friend/removeBlacklist", Map.of("id", id)), null, ANY);
    }
}
