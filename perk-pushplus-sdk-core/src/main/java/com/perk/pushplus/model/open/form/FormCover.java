package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单封面页配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormCover {

    private Boolean enabled;
    private String image;
    private String buttonText;
}
