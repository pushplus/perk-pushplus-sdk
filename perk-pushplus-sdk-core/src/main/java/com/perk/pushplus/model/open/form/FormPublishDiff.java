package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 草稿题目与发布快照差异。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormPublishDiff {

    private Boolean dirty;
    private Boolean breaking;
    private Integer responseCount;
    @Builder.Default
    private List<String> added = new ArrayList<>();
    @Builder.Default
    private List<String> removed = new ArrayList<>();
    @Builder.Default
    private List<String> typeChanged = new ArrayList<>();
    @Builder.Default
    private List<String> optionChanged = new ArrayList<>();
}
