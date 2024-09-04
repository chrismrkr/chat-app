package websocket.example.chatting_server.chatroom.unit.service.mock;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;

public class MockChatRoomEventHandler implements ChatRoomEventHandler {

    @Override
    public void publishEmptyCheck(Long roomId) {

    }

    @Override
    public void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment) {

    }
}
