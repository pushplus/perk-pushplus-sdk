package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.access.AccessKeyManager;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.PageQuery;
import io.github.perk.pushplus.model.PageResult;
import io.github.perk.pushplus.model.open.friend.FriendItem;
import io.github.perk.pushplus.model.open.friend.FriendQrCode;

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
}
