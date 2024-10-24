package websocket.example.chatting_server.chatroom.unit.service.mock;

import org.redisson.api.RLock;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.ArrayList;
import java.util.List;

public class MockChatRoomCacheRepository implements ChatRoomCacheRepository {
    private List<ChatHistory> chatHistoryCache = new ArrayList<>();

    @Override
    public RLock getChatRoomHistoryLock(Long roomId) {
        return null;
    }

    @Override
    public ChatHistory writeChatHistory(Long roomId, ChatHistory chatHistory) {
        return null;
    }

    @Override
    public List<ChatHistory> readChatHistory(Long roomId) {
        return List.of();
    }
}
