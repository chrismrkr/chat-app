package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryEsRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatHistoryRepositoryImpl implements ChatHistoryRepository {
    private final ChatHistoryEsRepository chatHistoryEsRepository;

    @Override
    public List<ChatHistory> findByRoomIdAndSeqLessThan(Long roomId, Long currentSeq, int size) {
        List<ChatHistoryEntity> byRoomIdAndSeqLessThan = chatHistoryEsRepository
                .findByRoomIdAndSeqLessThanOrderBySeqDesc(roomId, currentSeq, PageRequest.of(0, size));

        return byRoomIdAndSeqLessThan.stream()
                .map(ChatHistory::from)
                .sorted((h1, h2) -> h1.getSeq() < h2.getSeq() ? -1 : 1)
                .toList();
    }

    @Override
    public List<ChatHistory> findByRoomIdAndSendTimeAfter(Long roomId, LocalDateTime at) {
        List<ChatHistoryEntity> byRoomIdAndSendTimeAfter = chatHistoryEsRepository
                .findByRoomIdAndSendTimeAfterOrderBySeq(roomId, at);
        return byRoomIdAndSendTimeAfter.stream()
                .map(ChatHistory::from)
                .toList();
    }

    @Override
    public List<ChatHistory> findByRoomIdOrderBySeq(Long roomId) {
        List<ChatHistoryEntity> byRoomIdOrderBySeq = chatHistoryEsRepository.findByRoomIdOrderBySeq(roomId);
        return byRoomIdOrderBySeq.stream()
                .map(ChatHistory::from)
                .toList();
    }

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
