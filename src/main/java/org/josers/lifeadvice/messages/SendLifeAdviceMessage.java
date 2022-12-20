package org.josers.lifeadvice.messages;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendLifeAdviceMessage {
    private String from;
    private String to;
}
