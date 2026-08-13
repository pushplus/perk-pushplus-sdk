package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 保存表单设计。{@code items} 为题目列表，每题至少含 id、type、label。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSaveRequest {

    private Long id;
    private String title;
    private String description;
    private List<Map<String, Object>> items;
    private FormTheme theme;
    private FormSettings settings;
}
