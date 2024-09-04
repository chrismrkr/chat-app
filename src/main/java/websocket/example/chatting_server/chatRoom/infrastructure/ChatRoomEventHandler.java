package websocket.example.chatting_server.chatRoom.infrastructure;

public interface ChatRoomEventHandler {
    void publishEmptyCheck(Long roomId);
    void subscribeEmptyCheck(Long roomId);
}
