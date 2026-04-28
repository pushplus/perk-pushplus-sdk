package io.github.perk.pushplus.model.callback;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组新增用户回调中的 topicUserInfo 字段。
 */
@Data
@NoArgsConstructor
public class TopicUserInfo {

    private Long id;
    private String openId;
    private Long topicId;
    private Integer userSex;
    private Integer isFollow;
    private String nickName;
    private Integer havePhone;
    private String topicCode;
    private String topicName;
    private String headImgUrl;
    private Integer emailStatus;
}
