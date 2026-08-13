package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 表单详情（含草稿题目、主题、设置）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDetail {

    private Long id;
    private String formCode;
    private String fillUrl;
    private String title;
    private String description;
    private List<Map<String, Object>> items;
    private FormTheme theme;
    private FormSettings settings;
    private Integer status;
    private Boolean publishDirty;
    private Integer responseCount;
    private String publishTime;
    private String createTime;
    private String updateTime;
}
