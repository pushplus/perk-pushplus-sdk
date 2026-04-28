package io.github.perk.pushplus.model.open.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组内用户。
 */
@Data
@NoArgsConstructor
public class TopicUserItem {
    private Long id;
    private String nickName;
    private String openId;
    private String headImgUrl;
    private Integer userSex;
    private Integer havePhone;
    private Integer isFollow;
    private Integer emailStatus;
    private String followTime;
    private String remark;
}
