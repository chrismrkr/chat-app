package websocket.example.chatting_server.chat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.service.ChatHistoryService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private final ChatHistoryRepository chatHistoryRepository;
    @Override
    public ChatHistory write(ChatDto chatDto) {
        ChatHistory newHistory = ChatHistory.builder()
                .seq(chatDto.getSeq())
                .roomId(chatDto.getRoomId())
                .senderName(chatDto.getSenderName())
                .message(chatDto.getMessage())
                .sendTime(LocalDateTime.now())
                .build();
        return chatHistoryRepository.save(newHistory);
    }
}
