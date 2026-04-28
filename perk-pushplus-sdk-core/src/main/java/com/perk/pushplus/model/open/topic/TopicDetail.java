package com.perk.pushplus.model.open.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 群组详情（我创建的 / 我加入的复用此类，未返回字段为 null）。
 */
@Data
@NoArgsConstructor
public class TopicDetail {
    private Long topicId;
    private String topicName;
    private String topicCode;
    private String qrCodeImgUrl;
    private String contact;
    private String introduction;
    private String receiptMessage;
    private String nickName;
    private String createTime;
    private Integer topicUserCount;
    private String icon;
    private String appId;
    private Integer topicType;
    private BigDecimal price;
    private String topicDescribe;
    private String userNickName;
    private Integer isApproved;
    private Integer firstIsApproved;
    private String approveReason;
    private Integer isOpen;
}
