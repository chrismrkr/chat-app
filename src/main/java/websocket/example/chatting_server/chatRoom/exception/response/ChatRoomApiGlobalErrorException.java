package websocket.example.chatting_server.chatRoom.exception.response;

import lombok.Data;

@Data
public class ChatRoomApiGlobalErrorException {
    private final String message;
}
