package websocket.example.chatting_server.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.controller.port.MessageBrokerConsumeService;

@Service
@RequiredArgsConstructor
public class MessageBrokerConsumeServiceImpl implements MessageBrokerConsumeService {
//    @Value("${spring.kafka.consumer.topic-name}")
//    private String topic;
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
    private final SimpMessagingTemplate messagingTemplate;
//    @KafkaListener(topics = "${spring.kafka.consumer.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    @KafkaListener(topics = "chat-topic", groupId = "consumer-group1")
    public void consume(ChatDto chatDto) {
        String roomId = Long.toString(chatDto.getRoomId());
        messagingTemplate.convertAndSend("/chatroom/" + roomId, chatDto);
    }
}
