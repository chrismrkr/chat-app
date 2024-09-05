package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.OnClose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import websocket.example.chatting_server.chat.utils.KafkaConsumerConfigUtils;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ChatRoomEventHandlerImpl implements ChatRoomEventHandler {

    private final KafkaTemplate<Long, Long> kafkaTemplate;
    private final KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    @Autowired
    public ChatRoomEventHandlerImpl(@Qualifier("chatRoomEmptyCheckKafkaProducerTemplate")
                                        KafkaTemplate<Long, Long> kafkaTemplate,
                                    @Qualifier("chatRoomEmptyCheckConsumerConfig")
                                        KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig,
                                    ChatRoomRepository chatRoomRepository,
                                    MemberChatRoomRepository memberChatRoomRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatRoomEmptyCheckConsumerConfig = chatRoomEmptyCheckConsumerConfig;
        this.chatRoomRepository = chatRoomRepository;
        this.memberChatRoomRepository = memberChatRoomRepository;
    }

    @Override
    public void publishEmptyCheck(Long roomId) {
        String topicName = chatRoomEmptyCheckConsumerConfig.getTopicName();
        kafkaTemplate.send(topicName, roomId, roomId);
    }

    @Override
    @KafkaListener(
            topics = {"#{chatRoomEmptyCheckConsumerConfig.topicName}"},
            groupId = "#{chatRoomEmptyCheckConsumerConfig.groupId}",
            containerFactory = "chatRoomEmptyCheckKafkaListenerContainerFactory"
    )
    public void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment) {
        Long roomId = record.value();
        List<MemberChatRoom> participants = memberChatRoomRepository.findByRoomId(roomId);
        if(participants.isEmpty()) {
            chatRoomRepository.delete(roomId);
        }
        acknowledgment.acknowledge();
    }
}
