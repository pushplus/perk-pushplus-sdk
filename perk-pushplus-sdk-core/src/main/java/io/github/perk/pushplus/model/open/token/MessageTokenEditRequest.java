package io.github.perk.pushplus.model.open.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTokenEditRequest {

    private Long id;
    private String name;
    private String expireTime;
}
