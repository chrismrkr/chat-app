package websocket.example.chatting_server.chatRoom.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatRoomExitEvent {
    private Long roomId;
}
