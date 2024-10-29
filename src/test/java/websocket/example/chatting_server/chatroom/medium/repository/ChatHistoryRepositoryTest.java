package websocket.example.chatting_server.chatroom.medium.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Slf4j
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
        try {
            for (int i = 0; i < chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqArr[i] = seq;
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
        }
        finally {
            for(int i=0; i<chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(seqArr[i]);
            }
            Assertions.assertEquals(chatHistoryRepository.findByRoomId(roomId).size(), 0);
        }
    }

    @Test
    void chatHistory를_roomId로_조회하고_seq로_정렬() {
        // given
        int chatHistoryNumber = 50;
        long roomId = 3L;
        long[] seqArr = new long[chatHistoryNumber];
        try {
            for (int i = 0; i < chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqArr[i] = seq;
                chatHistoryRepository.save(ChatHistory.builder()
                        .seq(seq)
                        .roomId(roomId)
                        .senderName("test")
                        .message("hello world")
                        .build());
            }

            // when
            List<ChatHistory> byRoomId = chatHistoryRepository.findByRoomIdOrderBySeq(roomId);

            // then
            Assertions.assertEquals(byRoomId.size(), chatHistoryNumber);
            for (int i = 1; i < chatHistoryNumber; i++) {
                Assertions.assertTrue(byRoomId.get(i - 1).getSeq() < byRoomId.get(i).getSeq());
            }
        } finally {
            for (int i = 0; i < chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(seqArr[i]);
            }
            Assertions.assertEquals(chatHistoryRepository.findByRoomId(roomId).size(), 0);
        }
    }

    @Test
    void chatHistory를_roomId_및_특정시간_뒤에_생성된_것_조회() throws InterruptedException {
        // given
        int chatHistoryNumber = 50;
        long roomId = 4L;
        LocalDateTime standardTime = null;
        ChatHistory[] chatHistoryArr = new ChatHistory[chatHistoryNumber];
        try {
            for (int i = 0; i < chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                ChatHistory save = chatHistoryRepository.save(ChatHistory.builder()
                        .seq(seq)
                        .roomId(roomId)
                        .senderName("test")
                        .message("hello world")
                        .sendTime(LocalDateTime.now())
                        .build());
                chatHistoryArr[i] = save;
                if (i == 24) {
                    Thread.sleep(100);
                    standardTime = LocalDateTime.now();
                    Thread.sleep(100);
                }
            }

            // when
            List<ChatHistory> byRoomIdAndSendTimeAfter = chatHistoryRepository
                    .findByRoomIdAndSendTimeAfter(roomId, standardTime);
            // then
            for(int i=0; i<byRoomIdAndSendTimeAfter.size()-1; i++) {
                int finalI = i;
                Assertions.assertTrue(() -> byRoomIdAndSendTimeAfter.get(finalI).getSendTime()
                        .isBefore(byRoomIdAndSendTimeAfter.get(finalI +1).getSendTime()));
                Assertions.assertTrue(() -> byRoomIdAndSendTimeAfter.get(finalI).getSeq() <
                        byRoomIdAndSendTimeAfter.get(finalI+1).getSeq());
                // 인덱스가 0에서 뒤로 갈수록 현재 시간과 가까워짐
            }
            Assertions.assertEquals(byRoomIdAndSendTimeAfter.size(), chatHistoryNumber / 2);
        } finally {
            for (int i = 0; i < chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(chatHistoryArr[i].getSeq());
            }
            Assertions.assertEquals(chatHistoryRepository.findByRoomId(roomId).size(), 0);
        }
    }

    @Test
    void chatHistory중_특정_roomId이고_특정_seq_이전_N개_조회함() throws InterruptedException {
        // given
        int chatHistoryNumber = 1000;
        long roomId = 5L;
        int size = 111;
        try {
            for (long i = 0; i < chatHistoryNumber; i++) {
                ChatHistory chatHistory = ChatHistory.builder()
                        .roomId(roomId)
                        .seq(chatIdGenerateUtils.nextId())
                        .senderName("kim")
                        .message("hello " + Long.toString(i))
                        .sendTime(LocalDateTime.now())
                        .build();
                chatHistoryRepository.save(chatHistory);
            }

            Thread.sleep(2000);

            long currentSeq = chatIdGenerateUtils.nextId();
            int remains = chatHistoryNumber;
            while(remains > 0) {
                // when
                LocalDateTime startTime = LocalDateTime.now();
                List<ChatHistory> list = chatHistoryRepository.findByRoomIdAndSeqLessThan(roomId, currentSeq, size);
                LocalDateTime endTime = LocalDateTime.now();
                log.info("[CONSUMED TIME for READING {} HISTORY] {}ms", list.size(), Duration.between(startTime, endTime).toMillis());
                remains -= list.size();
                // then
                for(int i=0; i<list.size()-1; i++) {
                    int finalI = i;
                    Assertions.assertTrue(() -> list.get(finalI).getSeq() < list.get(finalI+1).getSeq());
                    Assertions.assertTrue(() -> list.get(finalI).getSendTime().isBefore(list.get(finalI+1).getSendTime()));
                }
                currentSeq = list.get(0).getSeq();
            }
        } catch (Exception e) {
          throw e;
        } finally {
            for(long i=0; i<chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(i);
            }
        }
    }
}
