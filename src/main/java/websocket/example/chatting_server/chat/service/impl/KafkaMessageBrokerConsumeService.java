package websocket.example.chatting_server.chat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.service.MessageBrokerConsumeService;

@Service
@RequiredArgsConstructor
public class KafkaMessageBrokerConsumeService implements MessageBrokerConsumeService {
    private final SimpMessagingTemplate messagingTemplate;
    @Override
//    @KafkaListener(topics = "chat-topic", groupId = "consumer-group1")
    public void consume(ChatDto chatDto) {
        String roomId = Long.toString(chatDto.getRoomId());
        messagingTemplate.convertAndSend("/chatroom/" + roomId, chatDto);
    }
}
