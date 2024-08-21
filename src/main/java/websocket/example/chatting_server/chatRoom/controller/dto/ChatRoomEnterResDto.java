package websocket.example.chatting_server.chatRoom.controller.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEnterResDto {
    private String status;
    private LocalDateTime enterAt;

    @Builder
    public ChatRoomEnterResDto(String status, LocalDateTime enterAt) {
        this.enterAt = enterAt;
        this.status = status;
    }
}
