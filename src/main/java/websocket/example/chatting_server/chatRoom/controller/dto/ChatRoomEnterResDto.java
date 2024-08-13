package websocket.example.chatting_server.chatRoom.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEnterResDto {
    private String status;

    @Builder
    public ChatRoomEnterResDto(String status) {
        this.status = status;
    }
}
