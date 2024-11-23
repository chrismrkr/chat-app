package websocket.example.chatting_server.chatRoom.exception.response;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
@Getter
public class ChatRoomApiFieldErrorException {
    private final String exception;
    private final Map<String, String> messages;
    public ChatRoomApiFieldErrorException(String exception) {
        this.exception = exception;
        this.messages = new HashMap<>();
    }
}
