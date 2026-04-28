package io.github.perk.pushplus.model.open.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicItem {
    private String icon;
    private Long topicId;
    private String topicCode;
    private String topicName;
    private String nickName;
    private String createTime;
    private Integer topicUserCount;
    /** 0普通群组；1积分群组；2公开群组。 */
    private Integer topicType;
    private Integer isApproved;
    private Integer firstIsApproved;
    private String approveReason;
    /** 0否，1是。 */
    private Integer isOpen;
}
