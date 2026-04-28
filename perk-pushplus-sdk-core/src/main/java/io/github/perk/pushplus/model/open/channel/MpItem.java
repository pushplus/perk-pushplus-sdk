package io.github.perk.pushplus.model.open.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MpItem {
    private Long id;
    private String nickName;
    private String headImg;
    private String principalName;
    private String authorizationAppid;
    private String funcInfo;
    private Integer serviceType;
    private Integer verifyType;
    private String alias;
    private String updateTime;
}
