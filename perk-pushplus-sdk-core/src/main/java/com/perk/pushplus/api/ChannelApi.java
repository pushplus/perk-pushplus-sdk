package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.channel.CpItem;
import com.perk.pushplus.model.open.channel.MailDetail;
import com.perk.pushplus.model.open.channel.MailItem;
import com.perk.pushplus.model.open.channel.MpItem;

import java.util.Map;

/**
 * 开放接口 - 微信公众号/企业微信/邮箱渠道列表（文档「七. 渠道配置接口」 5-8）。
 */
public class ChannelApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<MpItem>>> MP_LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<CpItem>>> CP_LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<MailItem>>> MAIL_LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<MailDetail>> MAIL_DETAIL =
            new TypeReference<>() {};

    public ChannelApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 5. 微信公众号渠道列表。 */
    public PageResult<MpItem> mpList(PageQuery q) {
        return executeOpen("POST", "/api/open/mp/list", q == null ? new PageQuery() : q, MP_LIST);
    }

    /** 6. 企业微信应用渠道列表。 */
    public PageResult<CpItem> cpList(PageQuery q) {
        return executeOpen("POST", "/api/open/cp/list", q == null ? new PageQuery() : q, CP_LIST);
    }

    /** 7. 邮箱渠道列表。 */
    public PageResult<MailItem> mailList(PageQuery q) {
        return executeOpen("POST", "/api/open/mail/list", q == null ? new PageQuery() : q, MAIL_LIST);
    }

    /** 8. 邮箱渠道详情。 */
    public MailDetail mailDetail(long mailId) {
        return executeOpen("GET", appendQuery("/api/open/mail/detail", Map.of("mailId", mailId)), null, MAIL_DETAIL);
    }
}
