package websocket.example.chatting_server.chat.service;

import websocket.example.chatting_server.chat.controller.dto.ChatDto;

public interface MessageBrokerConsumeService {
    void consume(ChatDto chatDto);
}
