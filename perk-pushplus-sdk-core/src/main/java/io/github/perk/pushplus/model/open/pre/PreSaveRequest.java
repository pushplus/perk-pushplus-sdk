package io.github.perk.pushplus.model.open.pre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreSaveRequest {

    /** 修改时必填。 */
    private Long id;
    private String content;
    private String preName;
    private String preCode;
    /** 1-JavaScript。 */
    private Integer contentType;
}
