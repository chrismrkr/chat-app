package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import jakarta.websocket.OnClose;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import websocket.example.chatting_server.chat.utils.KafkaConsumerConfigUtils;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;

import java.util.concurrent.CompletableFuture;

@Component
public class ChatRoomEventHandlerImpl implements ChatRoomEventHandler {

    private final KafkaTemplate<Long, Long> kafkaTemplate;
    private final KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig;

    @Autowired
    public ChatRoomEventHandlerImpl(@Qualifier("kafkaLongLongTypeTemplate")
                                        KafkaTemplate<Long, Long> kafkaTemplate,
                                    @Qualifier("chatRoomEmptyCheckConsumerConfig")
                                        KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatRoomEmptyCheckConsumerConfig = chatRoomEmptyCheckConsumerConfig;
    }

    @Override
    public void publishEmptyCheck(Long roomId) {
        kafkaTemplate.send(chatRoomEmptyCheckConsumerConfig.getTopicName(), roomId, roomId);
    }

    @Override
    @KafkaListener(
            topics = {"#{'chatRoomEmptyCheckConsumerConfig.topicName'}"},
            groupId = "#{'chatRoomEmptyCheckConsumerConfig.groupId'}"
    )
    public void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment) {
        Long value = record.value();
        acknowledgment.acknowledge();
    }
}
