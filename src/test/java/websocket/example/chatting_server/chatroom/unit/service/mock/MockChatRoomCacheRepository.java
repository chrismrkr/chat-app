package websocket.example.chatting_server.chatroom.unit.service.mock;

import org.redisson.api.RLock;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MockChatRoomCacheRepository implements ChatRoomCacheRepository {
    private Set<ChatHistory> chatHistoryCache = new TreeSet<>();

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

    @Override
    public void deleteChatHistory(Long roomId, Long seq) {
        return;
    }
}
