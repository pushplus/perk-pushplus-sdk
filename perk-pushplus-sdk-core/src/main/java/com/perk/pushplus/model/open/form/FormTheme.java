package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单主题外观。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormTheme {

    private String primaryColor;
    private String backgroundColor;
    private String headerImage;
    private String backgroundImage;
    private FormCover cover;
}
