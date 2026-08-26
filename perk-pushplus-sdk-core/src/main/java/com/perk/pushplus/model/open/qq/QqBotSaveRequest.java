package com.perk.pushplus.model.open.qq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增/修改 QQ 机器人渠道配置请求（id 仅修改时使用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QqBotSaveRequest {

    private Long id;
    /** 配置名称，必填，最多 64 个字符。 */
    private String qqName;
    /** 配置编码，新增必填；仅支持字母、数字、下划线和中划线，创建后不可修改。 */
    private String qqCode;
    /** 发送类型；留空时 SDK 自动填 2（发到 QQ 群）。 */
    private Integer sendType;
    /** QQ 群编号，必填，取自 groupList 返回的 id。 */
    private Long qqGroupId;
}
