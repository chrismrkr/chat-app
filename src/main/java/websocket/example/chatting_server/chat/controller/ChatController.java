package websocket.example.chatting_server.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.controller.port.MessageBrokerProduceService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    @Value("${spring.kafka.consumer.topic-name}")
    private String topic;
    private final MessageBrokerProduceService messageBrokerProduceService;
    @MessageMapping("/message/{roomId}") // pub : /app/message/{roomId}
    public void sendToMessageBroker(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
        ChatDto dto = new ChatDto(Long.parseLong(roomId), chatDto.getSenderName(), chatDto.getMessage());
        messageBrokerProduceService.sendMessage(topic, dto);
    }

    @SendTo("/chatroom/{roomId}")  // sub
    public ChatDto publish(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
        return new ChatDto(Long.parseLong(roomId), chatDto.getSenderName(), chatDto.getMessage());
    }
}
