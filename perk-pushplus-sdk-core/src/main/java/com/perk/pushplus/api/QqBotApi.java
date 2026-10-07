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
import com.perk.pushplus.model.open.qq.QqBotInfo;
import com.perk.pushplus.model.open.qq.QqBotItem;
import com.perk.pushplus.model.open.qq.QqBotSaveRequest;
import com.perk.pushplus.model.open.qq.QqCustomBotRequest;
import com.perk.pushplus.model.open.qq.QqGroupItem;
import com.perk.pushplus.model.open.qq.QqMyBotList;

import java.util.List;
import java.util.Map;

/**
 * 开放接口 - QQ 机器人（文档「十. QQ机器人接口」）。
 *
 * <p>除 pushplus 官方机器人外，用户还可以添加自有机器人；带 {@code botAppId} 的方法用于指定要操作的机器人，
 * 不指定时按官方/默认机器人处理。
 */
public class QqBotApi extends OpenAbstractApi {

    /** 渠道配置发送类型：发给自己（需指定 botAppId）。 */
    public static final int SEND_TYPE_SELF = 1;

    /** 渠道配置发送类型：发送到 QQ 群，未指定 sendType 时的默认值。 */
    public static final int SEND_TYPE_QQ_GROUP = 2;

    private static final TypeReference<ApiResponse<QqMyBotList>> MY_BOTS =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<QqBotInfo>> BOT_INFO =
            new TypeReference<>() {};
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

    /** 1. 获取官方机器人的绑定链接与绑定码。 */
    public QqBotBindLink getBindLink() {
        return getBindLink(false, null);
    }

    /** 1. 获取官方机器人的绑定链接与绑定码；refresh 为 true 时旧绑定码失效并重新生成。 */
    public QqBotBindLink getBindLink(boolean refresh) {
        return getBindLink(refresh, null);
    }

    /** 1. 获取指定机器人的绑定链接与绑定码；botAppId 为空时为官方机器人。 */
    public QqBotBindLink getBindLink(boolean refresh, String botAppId) {
        Map<String, Object> q = params();
        if (refresh) {
            q.put("refresh", true);
        }
        q.put("botAppId", blankToNull(botAppId));
        return executeOpen("GET", appendQuery("/api/open/qqBot/getBindLink", q), null, BIND_LINK);
    }

    /** 2. 查询默认机器人的绑定状态。 */
    public QqBotBindInfo botInfo() {
        return botInfo(null);
    }

    /** 2. 查询指定机器人的绑定状态；botAppId 为空时为默认机器人。 */
    public QqBotBindInfo botInfo(String botAppId) {
        return executeOpen("GET", withBotAppId("/api/open/qqBot/botInfo", botAppId), null, BIND_INFO);
    }

    /** 3. 解绑官方 QQ 机器人。 */
    public void unbind() {
        unbind(null);
    }

    /** 3. 解绑指定 QQ 机器人；botAppId 为空时解绑官方机器人。 */
    public void unbind(String botAppId) {
        executeOpen("GET", withBotAppId("/api/open/qqBot/unbind", botAppId), null, ANY);
    }

    /** 4. 获取所有机器人已加入的 QQ 群列表。 */
    public List<QqGroupItem> groupList() {
        return groupList(null);
    }

    /** 4. 获取指定机器人已加入的 QQ 群列表；botAppId 为空时返回全部。 */
    public List<QqGroupItem> groupList(String botAppId) {
        return executeOpen("GET", withBotAppId("/api/open/qqBot/groupList", botAppId), null, GROUPS);
    }

    /** 5. 获取 QQ 机器人渠道配置列表。 */
    public PageResult<QqBotItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/qqBot/list", q == null ? new PageQuery() : q, CONFIG_LIST);
    }

    /** 6. 新增渠道配置：发到指定 QQ 群（sendType=2），或用指定机器人发给自己（sendType=1）。 */
    public void add(QqBotSaveRequest request) {
        executeOpen("POST", "/api/open/qqBot/add", withDefaultSendType(request), ANY);
    }

    /** 7. 修改渠道配置；配置编码不可修改，但需传原值。 */
    public void edit(QqBotSaveRequest request) {
        executeOpen("POST", "/api/open/qqBot/edit", withDefaultSendType(request), ANY);
    }

    /** 8. 删除渠道配置。 */
    public void delete(long id) {
        executeOpen("DELETE", appendQuery("/api/open/qqBot/delete", Map.of("id", id)), null, ANY);
    }

    /** 9. 我的 QQ 机器人列表：官方与自有机器人及其绑定状态、自有机器人接入信息。 */
    public QqMyBotList myBots() {
        return executeOpen("GET", "/api/open/qqBot/myBots", null, MY_BOTS);
    }

    /** 10. 校验自有机器人凭证并获取头像昵称，不会保存。 */
    public QqBotInfo previewCustomBot(QqCustomBotRequest request) {
        return executeOpen("POST", "/api/open/qqBot/customBot/preview", request, BOT_INFO);
    }

    /** 11. 添加自有机器人。 */
    public void addCustomBot(QqCustomBotRequest request) {
        executeOpen("POST", "/api/open/qqBot/customBot/add", request, ANY);
    }

    /** 12. 修改自有机器人的 AppSecret。 */
    public void editCustomBot(QqCustomBotRequest request) {
        executeOpen("POST", "/api/open/qqBot/customBot/edit", request, ANY);
    }

    /** 13. 刷新自有机器人的头像昵称。 */
    public void refreshCustomBot(String botAppId) {
        executeOpen("GET", appendQuery("/api/open/qqBot/customBot/refresh", Map.of("botAppId", botAppId)), null, ANY);
    }

    /** 14. 删除自有机器人，同时删除该机器人上的绑定、QQ 群与渠道配置。 */
    public void deleteCustomBot(String botAppId) {
        executeOpen("DELETE", appendQuery("/api/open/qqBot/customBot/delete", Map.of("botAppId", botAppId)), null, ANY);
    }

    /** 15. 设置默认 QQ 机器人，需已绑定；发送时不填 option 即用默认机器人发给自己。 */
    public void setDefault(String botAppId) {
        executeOpen("GET", appendQuery("/api/open/qqBot/setDefault", Map.of("botAppId", botAppId)), null, ANY);
    }

    private String withBotAppId(String path, String botAppId) {
        Map<String, Object> q = params();
        q.put("botAppId", blankToNull(botAppId));
        return appendQuery(path, q);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
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
                .botAppId(request.getBotAppId())
                .qqGroupId(request.getQqGroupId())
                .build();
    }
}
