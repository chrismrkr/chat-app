package websocket.example.chatting_server.chat.infrastructure;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import websocket.example.chatting_server.chat.infrastructure.entity.ChatHistoryEntity;

import java.util.List;

public interface ChatHistoryEsRepository extends ElasticsearchRepository<ChatHistoryEntity, Long> {
    @Query("{\"bool\": {\"must\": [{\"match\": {\"roomId\": \"?0\"}}]}}")
    List<ChatHistoryEntity> findByRoomId(Long roomId);
}
