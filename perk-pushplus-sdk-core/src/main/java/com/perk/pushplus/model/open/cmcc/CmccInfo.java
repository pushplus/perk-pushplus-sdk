package com.perk.pushplus.model.open.cmcc;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新消息 ClawBot 绑定状态。
 */
@Data
@NoArgsConstructor
public class CmccInfo {

    /** 是否已绑定；0-未绑定，1-已绑定。 */
    private Integer bound;
    /** 脱敏后的 API Key。 */
    private String apiKeyMasked;
    /** 绑定时间。 */
    private String createTime;
}
