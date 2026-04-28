package io.github.perk.pushplus.model.open.setting;

import io.github.perk.pushplus.enums.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDefaultItem {
    private Long id;
    private Channel channel;
    private String channelTxt;
    private String updateTime;
    private String name;
}
