package websocket.example.chatting_server.chatRoom.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatHistoryEsRepository extends ElasticsearchRepository<ChatHistoryEntity, Long> {
    List<ChatHistoryEntity> findByRoomIdAndSeqLessThanOrderBySeqDesc(Long roomId, Long seq, Pageable pageable);
    List<ChatHistoryEntity> findByRoomIdAndSendTimeAfterOrderBySeq(Long roomId, LocalDateTime sendTime);
    List<ChatHistoryEntity> findByRoomId(Long roomId);
    List<ChatHistoryEntity> findByRoomIdOrderBySeq(Long roomId);
    Page<ChatHistoryEntity> findByRoomIdOrderBySeq(Long roomId, Pageable pageable);
}
