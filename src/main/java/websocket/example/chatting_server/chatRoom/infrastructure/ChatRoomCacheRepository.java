package websocket.example.chatting_server.chatRoom.infrastructure;

public interface ChatRoomCacheRepository {
    boolean lockChatRoomHistory(String roomId);
    boolean unlockChatRoomHistory(String roomId);
}
