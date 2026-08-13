package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发布表单结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormPublishResult {

    private Long id;
    private String formCode;
    private String fillUrl;
    private String title;
    private Integer status;
    private Integer previousStatus;
    private Boolean publishDirty;
    private String publishTime;
}
