package com.perk.pushplus.model.open.setting;

import com.perk.pushplus.enums.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增/修改默认配置。修改时需带上 id。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDefaultSaveRequest {

    /** 默认配置编号（修改时必填）。 */
    private Long id;

    private Channel channel;
    private String option;
    private String pre;
    /** 消息令牌 id；用户令牌为 0。 */
    private Long tokenId;
}
