package websocket.example.chatting_server.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    @MessageMapping("/message/{roomId}")
    @SendTo("/chatroom/{roomId}")
    public ChatDto publish(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
        Thread.sleep(500);
        return new ChatDto(chatDto.getSenderName(), chatDto.getMessage());
    }
}
