package websocket.example.chatting_server.chat.controller.port;

import websocket.example.chatting_server.chat.controller.dto.ChatDto;

public interface MessageBrokerProduceService {
    void sendMessage(String topic, ChatDto chatDto);
}
