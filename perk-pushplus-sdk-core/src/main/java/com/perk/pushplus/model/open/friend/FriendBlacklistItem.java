package com.perk.pushplus.model.open.friend;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 好友黑名单列表项。
 */
@Data
@NoArgsConstructor
public class FriendBlacklistItem {

    /** 黑名单记录 ID；解除黑名单时使用。 */
    private Long id;
    /** 被拉黑好友 ID。 */
    private Long friendId;
    private String nickName;
    private String headImgUrl;
    /** 拉黑时间。 */
    private String createTime;
}
