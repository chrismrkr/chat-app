package websocket.example.chatting_server.chat.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
public class ChatDto {
    private Long roomId;
    private String message;
    private String senderName;
    private String senderSessionId;
    private Integer seq;

    @Builder
    public ChatDto(Long roomId, String message, String senderName, String senderSessionId, Integer seq) {
        this.roomId = roomId;
        this.message = message;
        this.senderName = senderName;
        this.senderSessionId = senderSessionId;
        this.seq = seq;
    }
}
