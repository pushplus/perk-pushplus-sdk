package com.perk.pushplus.model.open.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解封剩余时间。
 */
@Data
@NoArgsConstructor
public class UserLimitTime {
    /** 1-无限制，2-短期限制，3-永久限制。 */
    private Integer sendLimit;
    private String userLimitTime;
}
