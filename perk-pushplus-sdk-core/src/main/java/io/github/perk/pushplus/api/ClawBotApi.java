package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.access.AccessKeyManager;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.open.clawbot.ClawBotInfo;
import io.github.perk.pushplus.model.open.clawbot.ClawBotMessage;
import io.github.perk.pushplus.model.open.clawbot.ClawBotQrCode;

import java.util.List;
import java.util.Map;

/**
 * 开放接口 - 微信 ClawBot（文档「八. 微信ClawBot接口」）。
 */
public class ClawBotApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<ClawBotQrCode>> QR =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ClawBotInfo>> INFO =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<List<ClawBotMessage>>> MSGS =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public ClawBotApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 获取二维码。 */
    public ClawBotQrCode getBotQrcode() {
        return executeOpen("GET", "/api/open/clawBot/getBotQrcode", null, QR);
    }

    /** 2. 扫码结果查询。 */
    public void getQrcodeStatus(String qrcode) {
        executeOpen("GET", appendQuery("/api/open/clawBot/getQrcodeStatus", Map.of("getQrcodeStatus", qrcode)), null, ANY);
    }

    /** 3. 绑定详情。 */
    public ClawBotInfo botInfo() {
        return executeOpen("GET", "/api/open/clawBot/botInfo", null, INFO);
    }

    /** 4. 解绑。 */
    public void unbind() {
        executeOpen("GET", "/api/open/clawBot/unbind", null, ANY);
    }

    /** 5. 获取发送消息。 */
    public List<ClawBotMessage> getMsg() {
        return executeOpen("GET", "/api/open/clawBot/getMsg", null, MSGS);
    }
}
