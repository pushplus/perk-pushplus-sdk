package com.perk.pushplus.model.open.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人资料详情。
 */
@Data
@NoArgsConstructor
public class UserInfo {

    private String openId;
    private String unionId;
    private String nickName;
    private String headImgUrl;
    private Integer userSex;
    private String token;
    private String phoneNumber;
    private String email;
    private Integer emailStatus;
    private String birthday;
    private Integer points;

    /** 会员信息。 */
    private VipInfo vipInfo;

    /** 实名认证状态；0-未实名，1-已实名。 */
    private Integer verifyStatus;
}
