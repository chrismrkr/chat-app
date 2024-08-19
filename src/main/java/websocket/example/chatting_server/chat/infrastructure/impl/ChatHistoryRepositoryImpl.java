package websocket.example.chatting_server.chat.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryEsRepository;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.infrastructure.entity.ChatHistoryEntity;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatHistoryRepositoryImpl implements ChatHistoryRepository {
    private final ChatHistoryEsRepository chatHistoryEsRepository;
    @Override
    public List<ChatHistory> findByRoomId(Long roomId) {
        List<ChatHistoryEntity> byRoomId = chatHistoryEsRepository.findByRoomId(roomId);
        return byRoomId.stream()
                .map(ChatHistory::from)
                .toList();
    }

    @Override
    public ChatHistory save(ChatHistory chatHistory) {
        ChatHistoryEntity save = chatHistoryEsRepository.save(chatHistory.toEntity());
        return ChatHistory.from(save);
    }

    @Override
    public Optional<ChatHistory> findBySeq(Long seq) {
        return chatHistoryEsRepository.findById(seq)
                .stream().map(ChatHistory::from)
                .findAny();
    }

    @Override
    public void deleteBySeq(Long seq) {
        chatHistoryEsRepository.deleteById(seq);
    }

    @Override
    public boolean existsBySeq(Long seq) {
        return chatHistoryEsRepository.existsById(seq);
    }
}
