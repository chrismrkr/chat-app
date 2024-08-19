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
        List<ChatHistory> list = byRoomId.stream()
                .map(ChatHistory::from)
                .toList();
        list.sort((history1, history2) -> {
            long history1Timestamp = (history1.getSeq() >> ChatIdGenerateUtils.TIMESTAMP_SHIFT) & ChatIdGenerateUtils.TIMESTAMP_MASK;
            long history1Serial = history1.getSeq() & ChatIdGenerateUtils.SEQUENCE_MASK;
            long history2Timestamp = (history2.getSeq() >> ChatIdGenerateUtils.TIMESTAMP_SHIFT) & ChatIdGenerateUtils.TIMESTAMP_MASK;;
            long history2Serial = history2.getSeq() & ChatIdGenerateUtils.SEQUENCE_MASK;;
            if(history1Timestamp < history1Timestamp) return -1;
            else if(history1Timestamp > history2Timestamp) return 1;
            else {
                if(history1Serial < history2Serial) return -1;
                else return 1;
            }
        });
        return list;
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
