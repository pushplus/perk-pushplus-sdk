package com.perk.pushplus.model.open.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档 / 表格列表项。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocListItem {

    private String docCode;
    private String shareUrl;
    private String title;
    private Integer sharePerm;
    private Integer shareLogin;
    private Integer perm;
    private Boolean published;
    private String publishTime;
    private String createTime;
    private String updateTime;
}
