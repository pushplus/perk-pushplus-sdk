package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.qq.QqBotBindInfo;
import com.perk.pushplus.model.open.qq.QqBotBindLink;
import com.perk.pushplus.model.open.qq.QqBotItem;
import com.perk.pushplus.model.open.qq.QqBotSaveRequest;
import com.perk.pushplus.model.open.qq.QqGroupItem;

import java.util.List;
import java.util.Map;

/**
 * 开放接口 - QQ 机器人（文档「九. QQ机器人接口」）。
 */
public class QqBotApi extends OpenAbstractApi {

    /** 发送到 QQ 群，目前渠道配置仅支持该类型。 */
    public static final int SEND_TYPE_QQ_GROUP = 2;

    private static final TypeReference<ApiResponse<QqBotBindLink>> BIND_LINK =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<QqBotBindInfo>> BIND_INFO =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<List<QqGroupItem>>> GROUPS =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<QqBotItem>>> CONFIG_LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public QqBotApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 获取绑定链接与绑定码。 */
    public QqBotBindLink getBindLink() {
        return getBindLink(false);
    }

    /** 1. 获取绑定链接与绑定码；refresh 为 true 时旧绑定码失效并重新生成。 */
    public QqBotBindLink getBindLink(boolean refresh) {
        String path = "/api/open/qqBot/getBindLink";
        if (refresh) {
            path = appendQuery(path, Map.of("refresh", true));
        }
        return executeOpen("GET", path, null, BIND_LINK);
    }

    /** 2. 查询绑定状态。 */
    public QqBotBindInfo botInfo() {
        return executeOpen("GET", "/api/open/qqBot/botInfo", null, BIND_INFO);
    }

    /** 3. 解绑 QQ 机器人。 */
    public void unbind() {
        executeOpen("GET", "/api/open/qqBot/unbind", null, ANY);
    }

    /** 4. 获取机器人已加入的 QQ 群列表。 */
    public List<QqGroupItem> groupList() {
        return executeOpen("GET", "/api/open/qqBot/groupList", null, GROUPS);
    }

    /** 5. 获取 QQ 机器人渠道配置列表。 */
    public PageResult<QqBotItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/qqBot/list", q == null ? new PageQuery() : q, CONFIG_LIST);
    }

    /** 6. 新增渠道配置，用于把消息发送到指定 QQ 群；发给自己无需创建配置。 */
    public void add(QqBotSaveRequest request) {
        executeOpen("POST", "/api/open/qqBot/add", withDefaultSendType(request), ANY);
    }

    /** 7. 修改渠道配置；配置编码不可修改。 */
    public void edit(QqBotSaveRequest request) {
        executeOpen("POST", "/api/open/qqBot/edit", withDefaultSendType(request), ANY);
    }

    /** 8. 删除渠道配置。 */
    public void delete(long id) {
        executeOpen("DELETE", appendQuery("/api/open/qqBot/delete", Map.of("id", id)), null, ANY);
    }

    private static QqBotSaveRequest withDefaultSendType(QqBotSaveRequest request) {
        if (request == null || request.getSendType() != null) {
            return request;
        }
        return QqBotSaveRequest.builder()
                .id(request.getId())
                .qqName(request.getQqName())
                .qqCode(request.getQqCode())
                .sendType(SEND_TYPE_QQ_GROUP)
                .qqGroupId(request.getQqGroupId())
                .build();
    }
}
