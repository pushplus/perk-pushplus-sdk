package io.github.perk.pushplus.model.open.friend;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendItem {
    private Long id;
    private Long friendId;
    private String token;
    private String headImgUrl;
    private String nickName;
    private Integer emailStatus;
    private Integer havePhone;
    private Integer isFollow;
    private String remark;
    private String createTime;
}
