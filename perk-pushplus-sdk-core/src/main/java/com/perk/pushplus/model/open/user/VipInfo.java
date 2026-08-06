package com.perk.pushplus.model.open.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员信息。
 */
@Data
@NoArgsConstructor
public class VipInfo {

    /** 是否会员；0-否，1-是。 */
    private Integer isVip;

    /** 会员到期日。 */
    private String lastDay;
}
