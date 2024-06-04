package websocket.example.chatting_server.chat.controller.port;

import websocket.example.chatting_server.chat.controller.dto.ChatDto;

public interface MessageBrokerProducerService {
    public void sendMessage(String topic, ChatDto chatDto);
}
