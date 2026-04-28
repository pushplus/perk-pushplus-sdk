package io.github.perk.pushplus.model.open.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailItem {
    private Long id;
    private String mailName;
    private String mailCode;
}
