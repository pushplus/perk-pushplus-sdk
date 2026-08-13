package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单收集 / 展示设置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSettings {

    private String endTime;
    private Integer maxResponses;
    private Boolean oncePerUser;
    private Boolean allowAnonymous;
    private String password;
    private Boolean showQuestionNumber;
    private Boolean onePerPage;
    private Boolean showPrevButton;
    private Boolean hideTitle;
    private Boolean hideCopyright;
    private Boolean hideAd;
    private Boolean showOutline;
    private String thankText;
    private Boolean redirectEnabled;
    private String redirectUrl;
    private Boolean allowEdit;
}
