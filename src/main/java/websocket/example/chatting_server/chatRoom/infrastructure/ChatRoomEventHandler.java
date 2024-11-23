package websocket.example.chatting_server.chatRoom.infrastructure;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import websocket.example.chatting_server.chatRoom.service.event.ChatRoomExitEvent;

public interface ChatRoomEventHandler {
    void publishEmptyCheck(ChatRoomExitEvent event);
    void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment);
    void checkEmpty(Long roomId);
}
