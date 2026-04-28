package com.perk.pushplus.model.open.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicAddRequest {

    private String topicCode;
    private String topicName;
    private String contact;
    private String introduction;
    private String receiptMessage;
    private String appId;
    private String icon;
    /** 0普通；1积分；2公开。默认 0。 */
    private Integer topicType;
    private BigDecimal price;
    private String topicDescribe;
}
