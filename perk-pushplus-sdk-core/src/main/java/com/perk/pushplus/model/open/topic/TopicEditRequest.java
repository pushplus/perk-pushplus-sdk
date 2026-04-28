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
public class TopicEditRequest {

    /** 群组编号，必填。 */
    private Long topic;

    private String topicCode;
    private String topicName;
    private String contact;
    private String introduction;
    private String receiptMessage;
    private String icon;
    private BigDecimal price;
    private String topicDescribe;
}
