package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.setting.UserDefaultDetail;
import com.perk.pushplus.model.open.setting.UserDefaultItem;
import com.perk.pushplus.model.open.setting.UserDefaultSaveRequest;

import java.util.Map;

/**
 * 开放接口 - 功能设置（文档「九. 功能设置接口」）。
 */
public class SettingApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<UserDefaultItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<UserDefaultDetail>> DETAIL =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public SettingApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 获取默认配置列表。 */
    public PageResult<UserDefaultItem> listUserDefault(PageQuery q) {
        return executeOpen("POST", "/api/open/setting/listUserDefault", q == null ? new PageQuery() : q, LIST);
    }

    /** 2. 默认配置详情。 */
    public UserDefaultDetail detailUserDefault(long id) {
        return executeOpen("GET", appendQuery("/api/open/setting/detailUserDefault", Map.of("id", id)), null, DETAIL);
    }

    /** 3. 新增默认配置。 */
    public void addUserDefault(UserDefaultSaveRequest req) {
        executeOpen("POST", "/api/open/setting/addUserDefault", req, ANY);
    }

    /** 4. 修改默认配置。 */
    public void editUserDefault(UserDefaultSaveRequest req) {
        executeOpen("POST", "/api/open/setting/editUserDefault", req, ANY);
    }

    /** 5. 删除默认配置。 */
    public void deleteUserDefault(long id) {
        executeOpen("DELETE", appendQuery("/api/open/setting/deleteUserDefault", Map.of("id", id)), null, ANY);
    }

    /**
     * 6. 修改接收消息限制。
     *
     * @param recevieLimit 0-接收全部，1-不接收消息
     */
    public void changeReceiveLimit(int recevieLimit) {
        executeOpen("GET", appendQuery("/api/open/setting/changeRecevieLimit", Map.of("recevieLimit", recevieLimit)), null, ANY);
    }

    /**
     * 7. 开启/关闭发送消息功能。
     *
     * @param isSend 0-禁用，1-启用
     */
    public void changeIsSend(int isSend) {
        executeOpen("GET", appendQuery("/api/open/setting/changeIsSend", Map.of("isSend", isSend)), null, ANY);
    }

    /**
     * 8. 修改打开消息方式。
     *
     * @param openMessageType 0:H5，1:小程序
     */
    public void changeOpenMessageType(int openMessageType) {
        executeOpen("GET", appendQuery("/api/open/setting/changeOpenMessageType", Map.of("openMessageType", openMessageType)), null, ANY);
    }

    /**
     * 9. 修改插件渠道转发。
     *
     * @param forward 0:否，1:是
     */
    public void changeExtensionForward(int forward) {
        executeOpen("GET", appendQuery("/api/open/setting/extension", Map.of("forward", forward)), null, ANY);
    }
}
