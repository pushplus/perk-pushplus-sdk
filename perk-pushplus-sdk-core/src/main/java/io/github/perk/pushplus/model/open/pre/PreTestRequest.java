package io.github.perk.pushplus.model.open.pre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreTestRequest {
    private String content;
    private Integer contentType;
    private String message;
}
