package websocket.example.chatting_server.chatRoom.infrastructure;

import websocket.example.chatting_server.chatRoom.domain.ChatHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatHistoryRepository {
    List<ChatHistory> findByRoomIdAndSeqLessThan(Long roomId, Long currentSeq, int size);
    List<ChatHistory> findByRoomIdAndSendTimeAfter(Long roomId, LocalDateTime at);
    List<ChatHistory> findByRoomIdOrderBySeq(Long roomId);
    List<ChatHistory> findByRoomId(Long roomId);
    ChatHistory save(ChatHistory chatHistory);
    Optional<ChatHistory> findBySeq(Long seq);
    void deleteBySeq(Long seq);
    boolean existsBySeq(Long seq);
}
