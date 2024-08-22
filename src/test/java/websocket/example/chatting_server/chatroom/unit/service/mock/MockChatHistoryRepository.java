package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MockChatHistoryRepository implements ChatHistoryRepository {
    @Override
    public List<ChatHistory> findByRoomIdAndSendTimeAfter(Long roomId, LocalDateTime at) {
        return null;
    }

    @Override
    public List<ChatHistory> findByRoomIdOrderBySeq(Long roomId) {
        return null;
    }

    @Override
    public List<ChatHistory> findByRoomId(Long roomId) {
        return null;
    }

    @Override
    public ChatHistory save(ChatHistory chatHistory) {
        return null;
    }

    @Override
    public Optional<ChatHistory> findBySeq(Long seq) {
        return Optional.empty();
    }

    @Override
    public void deleteBySeq(Long seq) {

    }

    @Override
    public boolean existsBySeq(Long seq) {
        return false;
    }
}
