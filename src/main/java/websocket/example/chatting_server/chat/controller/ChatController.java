package websocket.example.chatting_server.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final KafkaTemplate<String, ChatDto> kafkaTemplate;

    @MessageMapping("/message/{roomId}") // pub : /app/message/{roomId}
//    public void sendToMessageBroker(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
//        kafkaTemplate.send(roomId, chatDto);
//    }
//
    @SendTo("/chatroom/{roomId}")  // sub
    public ChatDto publish(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
        Thread.sleep(500);
        return new ChatDto(chatDto.getSenderName(), chatDto.getMessage());
    }
}
