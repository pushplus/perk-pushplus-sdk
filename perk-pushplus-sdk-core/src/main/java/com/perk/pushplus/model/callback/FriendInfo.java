package com.perk.pushplus.model.callback;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增好友回调中的 friendInfo 字段。
 */
@Data
@NoArgsConstructor
public class FriendInfo {

    private String token;
    private Long friendId;
    private Integer isFollow;
    private String nickName;
    private Integer havePhone;
    private String createTime;
    private Integer emailStatus;
}
