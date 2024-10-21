package websocket.example.chatting_server.chatroom.unit.service.mock;

import org.redisson.api.RLock;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

public class MockChatRoomCacheRepository implements ChatRoomCacheRepository {
    @Override
    public RLock getChatRoomHistoryLock(Long roomId) {
        return null;
    }

    @Override
    public ChatHistory write(Long roomId, ChatHistory chatHistory) {
        return null;
    }
}
