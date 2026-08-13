package com.perk.pushplus.model.open.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表格内容（整表 JSON 字符串草稿）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelContent {

    private String docCode;
    private String shareUrl;
    private String title;
    private Integer sharePerm;
    private Integer shareLogin;
    private Integer perm;
    private Boolean published;
    private Boolean publishDirty;
    private String publishTime;
    private String createTime;
    private String updateTime;
    /** 整表草稿 JSON 字符串。 */
    private String content;
}
