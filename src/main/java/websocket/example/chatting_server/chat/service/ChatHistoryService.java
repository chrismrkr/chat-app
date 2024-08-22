package websocket.example.chatting_server.chat.service;

import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.domain.ChatHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatHistoryService {
    ChatHistory write(ChatDto chatDto);
}
