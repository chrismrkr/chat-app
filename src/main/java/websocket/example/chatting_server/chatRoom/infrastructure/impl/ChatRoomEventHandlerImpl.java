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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import websocket.example.chatting_server.chat.utils.KafkaConsumerConfigUtils;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.event.ChatRoomExitEvent;

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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEmptyCheck(ChatRoomExitEvent chatRoomExitEvent) {
        String topicName = chatRoomEmptyCheckConsumerConfig.getTopicName();
        Long roomId = chatRoomExitEvent.getRoomId();
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
        log.info("ROOM {} participants: {}", roomId, participants.size());
        if(participants.isEmpty()) {
            chatRoomRepository.delete(roomId);
        }
        acknowledgment.acknowledge();
    }
}
