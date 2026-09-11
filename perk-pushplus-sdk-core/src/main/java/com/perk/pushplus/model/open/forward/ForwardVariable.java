package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 模板变量。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardVariable {

    private Long id;
    private Long ruleId;
    /** 变量名，模板中用 {@code {{变量名}}} 引用。 */
    private String varName;
    /** 变量来源；1-请求头，2-Query参数，3-请求体，4-URL路径，5-主题（邮件）。 */
    private Integer sourceType;
    /** 提取方式；1-序列化数据，2-正则表达式，3-JSONPath，4-原始全文。 */
    private Integer extractType;
    /** 键 / 正则 / JSONPath 表达式。 */
    private String extractKey;
    /** 提取不到时的默认值。 */
    private String defaultValue;
    /** 提取顺序。 */
    private Integer sort;
}
