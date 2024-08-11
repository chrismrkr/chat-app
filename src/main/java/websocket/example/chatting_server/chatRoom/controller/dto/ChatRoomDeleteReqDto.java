package websocket.example.chatting_server.chatRoom.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatRoomDeleteReqDto {
    private Long roomId;
    private Long memberId;
}
