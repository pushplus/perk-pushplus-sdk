package com.perk.pushplus.model.open.pre;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreItem {
    private Long id;
    private String preName;
    private String preCode;
    /** 1-JavaScript。 */
    private Integer contentType;
    private String createTime;
}
