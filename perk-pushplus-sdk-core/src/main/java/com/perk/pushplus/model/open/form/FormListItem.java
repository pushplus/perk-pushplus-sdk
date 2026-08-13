package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单列表项 / 创建、复制结果（不含题目明细）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormListItem {

    private Long id;
    private String formCode;
    private String fillUrl;
    private String title;
    private String description;
    private Integer status;
    private Integer responseCount;
    private String publishTime;
    private String createTime;
    private String updateTime;
}
