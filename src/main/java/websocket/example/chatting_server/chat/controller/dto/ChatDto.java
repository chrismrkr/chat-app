package websocket.example.chatting_server.chat.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatDto {
    private Long roomId;
    private String senderName;
    private String message;
}
