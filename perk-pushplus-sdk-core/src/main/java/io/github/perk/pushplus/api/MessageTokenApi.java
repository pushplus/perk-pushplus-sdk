package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.access.AccessKeyManager;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.PageQuery;
import io.github.perk.pushplus.model.PageResult;
import io.github.perk.pushplus.model.open.token.MessageTokenAddRequest;
import io.github.perk.pushplus.model.open.token.MessageTokenEditRequest;
import io.github.perk.pushplus.model.open.token.MessageTokenItem;
import io.github.perk.pushplus.model.open.token.MessageTokenOption;

import java.util.List;
import java.util.Map;

/**
 * 开放接口 - 消息 token（文档「四. 消息token接口」）。
 */
public class MessageTokenApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<MessageTokenItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<List<MessageTokenOption>>> OPTIONS =
            new TypeReference<>() {};

    public MessageTokenApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 获取消息 token 列表。 */
    public PageResult<MessageTokenItem> list(PageQuery query) {
        return executeOpen("POST", "/api/open/token/list", query == null ? new PageQuery() : query, LIST);
    }

    /** 新增消息 token，返回新建的 token 字符串。 */
    public String add(MessageTokenAddRequest req) {
        return executeOpen("POST", "/api/open/token/add", req, STRING);
    }

    /** 修改消息 token。 */
    public String edit(MessageTokenEditRequest req) {
        return executeOpen("POST", "/api/open/token/edit", req, STRING);
    }

    /** 删除消息 token。 */
    public String delete(long id) {
        var path = appendQuery("/api/open/token/deleteToken", Map.of("id", id));
        return executeOpen("DELETE", path, null, STRING);
    }

    /**
     * 消息 token 下拉选择列表。
     *
     * @param type 0-返回所有；1-返回未配置默认推送渠道的消息 token
     */
    public List<MessageTokenOption> selectList(Integer type) {
        var path = appendQuery("/api/open/token/selectTokenList",
                Map.of("type", type == null ? 0 : type));
        return executeOpen("GET", path, null, OPTIONS);
    }
}
