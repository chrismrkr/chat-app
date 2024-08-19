package websocket.example.chatting_server.chat.infrastructure;

import websocket.example.chatting_server.chat.domain.ChatHistory;

import java.util.List;
import java.util.Optional;

public interface ChatHistoryRepository {
    List<ChatHistory> findByRoomId(Long roomId);
    ChatHistory save(ChatHistory chatHistory);
    Optional<ChatHistory> findBySeq(Long seq);
    void deleteBySeq(Long seq);
    boolean existsBySeq(Long seq);
}
