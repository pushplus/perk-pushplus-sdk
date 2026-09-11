package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.forward.ForwardRuleDetail;
import com.perk.pushplus.model.open.forward.ForwardRuleItem;
import com.perk.pushplus.model.open.forward.ForwardRuleSaveRequest;
import com.perk.pushplus.model.open.forward.ForwardRuleSetting;
import com.perk.pushplus.model.open.forward.ForwardRuleTestRequest;
import com.perk.pushplus.model.open.forward.ForwardRuleTestResult;

import java.util.Map;

/**
 * 开放接口 - 消息规则（文档「十四. 消息规则接口」）。注：需开通会员后才能开启。
 */
public class ForwardRuleApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<ForwardRuleItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ForwardRuleDetail>> DETAIL =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ForwardRuleTestResult>> TEST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ForwardRuleSetting>> SETTING =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public ForwardRuleApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 获取消息规则列表。 */
    public PageResult<ForwardRuleItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/forwardRule/list", q == null ? new PageQuery() : q, LIST);
    }

    /** 查看消息规则详情。 */
    public ForwardRuleDetail detail(long ruleId) {
        return executeOpen("GET", appendQuery("/api/open/forwardRule/detail", Map.of("ruleId", ruleId)), null, DETAIL);
    }

    /** 新增消息规则。 */
    public void add(ForwardRuleSaveRequest req) {
        executeOpen("POST", "/api/open/forwardRule/add", req, ANY);
    }

    /** 修改消息规则；会整体覆盖模板变量和发送目标。 */
    public void edit(ForwardRuleSaveRequest req) {
        executeOpen("POST", "/api/open/forwardRule/edit", req, ANY);
    }

    /** 删除消息规则。 */
    public void delete(long ruleId) {
        executeOpen("DELETE", appendQuery("/api/open/forwardRule/delete", Map.of("ruleId", ruleId)), null, ANY);
    }

    /**
     * 启用 / 停用消息规则。
     *
     * @param status 1-启用，0-停用
     */
    public void changeStatus(long ruleId, int status) {
        executeOpen("GET", appendQuery("/api/open/forwardRule/changeStatus",
                Map.of("ruleId", ruleId, "status", status)), null, ANY);
    }

    /** 用模拟请求测试规则，不会真正发送消息。 */
    public ForwardRuleTestResult test(ForwardRuleTestRequest req) {
        return executeOpen("POST", "/api/open/forwardRule/test", req, TEST);
    }

    /** 获取消息规则总开关模式。 */
    public ForwardRuleSetting getSetting() {
        return executeOpen("GET", "/api/open/forwardRule/setting", null, SETTING);
    }

    /**
     * 设置消息规则总开关模式。
     *
     * @param mode 0-关闭，1-开启且未命中仍推送，2-开启且未命中不推送
     */
    public void saveSetting(int mode) {
        executeOpen("GET", appendQuery("/api/open/forwardRule/setting", Map.of("mode", mode)), null, ANY);
    }
}
