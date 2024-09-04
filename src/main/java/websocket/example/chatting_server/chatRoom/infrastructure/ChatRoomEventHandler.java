package websocket.example.chatting_server.chatRoom.infrastructure;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface ChatRoomEventHandler {
    void publishEmptyCheck(Long roomId);
    void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment);
}
