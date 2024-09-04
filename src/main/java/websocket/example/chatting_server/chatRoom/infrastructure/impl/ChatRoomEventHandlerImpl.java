package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import jakarta.websocket.OnClose;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;

import java.util.concurrent.CompletableFuture;

@Component
public class ChatRoomEventHandlerImpl implements ChatRoomEventHandler {

    private final KafkaTemplate<Long, Long> kafkaTemplate;

    @Autowired
    public ChatRoomEventHandlerImpl(@Qualifier("kafkaLongLongTypeTemplate")
                                        KafkaTemplate<Long, Long> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEmptyCheck(Long roomId) {
        kafkaTemplate.send(emptyChatroomCheckTopic, roomId, roomId);
    }

    @Override
    @KafkaListener(
            topics = {"#{'chatRoomEventHandlerImpl.emptyChatroomCheckTopic'}"},
            groupId = "#{'chatRoomEventHandlerImpl.emptyChatroomCheckConsumerGroupId'}"
    )
    public void subscribeEmptyCheck(Long roomId) {

    }
}
