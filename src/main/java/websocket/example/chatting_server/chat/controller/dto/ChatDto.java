package websocket.example.chatting_server.chat.controller.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
public class ChatDto {
    private Long roomId;
    private String message;
    private String senderName;
    private String senderSessionId;
    private Long seq;
    private LocalDateTime createdAt;

    @Builder
    public ChatDto(Long roomId, String message, String senderName, String senderSessionId, Long seq, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.message = message;
        this.senderName = senderName;
        this.senderSessionId = senderSessionId;
        this.seq = seq;
        this.createdAt = createdAt;
    }
}
