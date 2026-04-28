package com.perk.pushplus.model.open.setting;

import com.perk.pushplus.enums.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDefaultDetail {
    private Long id;
    private Channel channel;
    private String option;
    private String pre;
    private String updateTime;
    private String name;
    private Long tokenId;
}
