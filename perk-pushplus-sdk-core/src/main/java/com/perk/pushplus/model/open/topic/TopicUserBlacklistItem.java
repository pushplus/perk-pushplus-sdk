package com.perk.pushplus.model.open.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组订阅人黑名单列表项。
 */
@Data
@NoArgsConstructor
public class TopicUserBlacklistItem {

    /** 黑名单记录 ID；解除黑名单时使用。 */
    private Long id;
    /** 被拉黑用户 ID。 */
    private Long userId;
    private String nickName;
    private String openId;
    private String headImgUrl;
    /** 拉黑时间。 */
    private String createTime;
}
