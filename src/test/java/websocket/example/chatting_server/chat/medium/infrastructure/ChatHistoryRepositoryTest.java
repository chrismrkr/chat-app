package websocket.example.chatting_server.chat.medium.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ChatHistoryRepositoryTest {
    @Autowired
    ChatHistoryRepository chatHistoryRepository;
    @Autowired
    ChatIdGenerateUtils chatIdGenerateUtils;

    @Test
    void chatHistory_저장_후_seq로_조회() {
        // given
        ChatHistory chatHistory1 = ChatHistory.builder()
                .seq(chatIdGenerateUtils.nextId()) // ex. 1825044961289043968L
                .roomId(1L)
                .senderName("name")
                .message("hello world")
                .build();
        // when
        ChatHistory save = chatHistoryRepository.save(chatHistory1);

        // then
        ChatHistory find = chatHistoryRepository.findBySeq(chatHistory1.getSeq()).get();
        Assertions.assertEquals(save.getSeq(), find.getSeq());
        Assertions.assertEquals(save.getRoomId(), find.getRoomId());
        Assertions.assertEquals(save.getSenderName(), find.getSenderName());
        Assertions.assertEquals(save.getMessage(), find.getMessage());
        // finally
        chatHistoryRepository.deleteBySeq(find.getSeq());
        Assertions.assertFalse(chatHistoryRepository.existsBySeq(find.getSeq()));
    }

    @Test
    void chatHistory를_roomId로_조회() {
        // given
        int chatHistoryNumber = 50;
        long roomId = 2L;
        long[] seqArr = new long[chatHistoryNumber];
        for(int i=0; i<chatHistoryNumber; i++) {
            long seq = chatIdGenerateUtils.nextId();
            chatHistoryRepository.save(ChatHistory.builder()
                    .seq(seq)
                    .roomId(roomId)
                    .senderName("test")
                    .message("hello world")
                    .build());
        }

        // when
        List<ChatHistory> byRoomId = chatHistoryRepository.findByRoomId(roomId);

        // then
        Assertions.assertEquals(byRoomId.size(), chatHistoryNumber);

        // finally
        for(int i=0; i<chatHistoryNumber; i++) {
            chatHistoryRepository.deleteBySeq(seqArr[i]);
        }
        Assertions.assertEquals(chatHistoryRepository.findByRoomId(roomId).size(), 0);
    }

}
