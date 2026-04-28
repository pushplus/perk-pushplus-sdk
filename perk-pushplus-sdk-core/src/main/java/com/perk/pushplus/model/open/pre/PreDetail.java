package com.perk.pushplus.model.open.pre;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreDetail {
    private Long id;
    private String preName;
    private String preCode;
    private Integer contentType;
    private String content;
}
