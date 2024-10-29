package websocket.example.chatting_server.chatroom.medium.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Rollback
public class ChatRoomServiceTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;
    @Autowired
    ChatHistoryRepository chatHistoryRepository;
    @Autowired
    ChatRoomCacheRepository chatRoomCacheRepository;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    ChatIdGenerateUtils chatIdGenerateUtils;

    @Test
    void chatroom_신규_생성_후_입장() {
        // given
        String roomName = "room1";
        Long memberId = 1L;

        // when
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);

        // then
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByMemberId(memberId).size(),
                1
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByMemberId(memberId).get(0).getMemberId(),
                memberId
        );
    }

    @Test
    void chatroom_입장() {
        // given
        String roomName = "room2";
        Long memberId = 2L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );
        // when
        Long newMemberId = 3L;
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).size(),
                2
        );
    }

    @Test
    void chatroom_퇴장() {
        // given
        String roomName = "room3";
        Long memberId = 3L;
        Long newMemberId = 4L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );

        // when
        chatRoomService.exit(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomIdWithChatRoom(chatRoom.getRoomId()).size(),
                1
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomIdWithChatRoom(chatRoom.getRoomId()).get(0).getMemberId(),
                memberId
        );
        Assertions.assertNotNull(chatRoomRepository.findById(chatRoom.getRoomId()));
    }

    @Test
    void chatroom_퇴장_시_남은_인원이_없으면_채팅방_자동_삭제() {
        // given
        String roomName = "room4";
        Long memberId = 5L;
        Long newMemberId = 6L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );

        // when
        chatRoomService.exit(memberId, chatRoom.getRoomId());
        chatRoomService.exit(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(memberChatRoomRepository.findByRoomIdWithChatRoom(chatRoom.getRoomId()).size(), 0);
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
                    Assertions.assertEquals(byId, Optional.empty());
                });
    }

    @Test
    void memberId로_chatroom_조회() {
        // given
        Long memberId1 = 8L;
        Long memberId2 = 9L;
        String[] roomNames = {"room7", "room8", "room9"};
        chatRoomService.create(memberId1, roomNames[0]);
        chatRoomService.create(memberId1, roomNames[1]);
        chatRoomService.create(memberId2, roomNames[2]);

        // when
        List<ChatRoom> byMemberId1 = chatRoomService.findByMemberId(memberId1);
        List<ChatRoom> byMemberId2 = chatRoomService.findByMemberId(memberId2);

        // then
        Assertions.assertEquals(byMemberId1.size(), 2);
        Assertions.assertEquals(byMemberId2.size(), 1);
        Assertions.assertEquals(byMemberId2.get(0).getRoomName(), roomNames[2]);
    }

    @Test
    void chatHistory를_RoomId_및_입장시간을_조건으로_조회() throws InterruptedException {
        // given
        Long memberId = 12L;
        Long newMemberId = 13L;
        String roomName = "room12";
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        List<Long> seqList = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                long seq = chatIdGenerateUtils.nextId();
                ChatHistory write = chatRoomService.writeChatHistory(
                        chatRoom.getRoomId(),
                        ChatDto.builder()
                                .seq(seq)
                                .roomId(chatRoom.getRoomId())
                                .senderName("member")
                                .message("hello")
                                .build()
                );
                seqList.add(seq);
            }

            // when
            Thread.sleep(100);
            chatRoomService.enter(newMemberId, chatRoom.getRoomId());
            Thread.sleep(100);
            for (int i = 0; i < 100; i++) {
                long seq = chatIdGenerateUtils.nextId();
                chatRoomService.writeChatHistory(
                        chatRoom.getRoomId(),
                        ChatDto.builder()
                                .seq(seq)
                                .roomId(chatRoom.getRoomId())
                                .senderName("member")
                                .message("hello")
                                .build()
                );
                seqList.add(seq);
            }

            // then
            List<ChatHistory> chatHistories = chatRoomService.readChatHistory(chatRoom.getRoomId(), newMemberId);
            Assertions.assertEquals(chatHistories.size(), 100);
        } finally {
            for (Long seq : seqList) {
                chatHistoryRepository.deleteBySeq(seq);
            }
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(chatRoom.getRoomId()));
            bucket.delete();
        }
    }

    @Test
    void chatroom에_동일한_member가_2번_이상_입장해도_최초_입장시간은_불변함() throws InterruptedException {
        // given
        Long memberId = 14L;
        Long newMemberId = 15L;
        String roomName = "room13";
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);

        // when
        Thread.sleep(100);
        MemberChatRoom enter1 = chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Thread.sleep(1000);
        MemberChatRoom enter2 = chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Thread.sleep(1000);
        MemberChatRoom enter3 = chatRoomService.enter(newMemberId, chatRoom.getRoomId());

        // then
        LocalDateTime localDateTime = memberChatRoomRepository.findEnterDateTime(newMemberId, chatRoom.getRoomId())
                .get();
        Assertions.assertEquals(enter1.getEnterDateTime().getSecond(), localDateTime.getSecond());
    }

    @Test
    void chatroom에서_동시에_모두_퇴장해도_정상적으로_chatroom이_삭제됨() throws InterruptedException {
        // given
        String roomName = "room14";
        Long memberId = 16L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        Long startMemberId = 17L;
        Long endMemberId = 99L;
        for(long i=startMemberId; i<=endMemberId; i++) {
            chatRoomService.enter(i, chatRoom.getRoomId());
        }

        // when
        chatRoomService.exit(memberId, chatRoom.getRoomId());
        long threadCount = endMemberId - startMemberId + 1;
        ExecutorService executorService = Executors.newFixedThreadPool((int) threadCount);
        CountDownLatch countDownLatch = new CountDownLatch((int) threadCount);

        for(long i=startMemberId; i<=endMemberId; i++) {
            long finalI = i;
            executorService.execute(() -> {
                chatRoomService.exit(finalI, chatRoom.getRoomId());
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomIdWithChatRoom(chatRoom.getRoomId());
        Assertions.assertEquals(byRoomId.size(), 0);
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
                    Assertions.assertEquals(byId, Optional.empty());
                });
    }

    @Test
    void chatroom_채팅_history가_저장된_cache를_읽을_수_있다() {
        String roomName = "room15";
        Long memberId = 17L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        int chatHistoryNumber = 500;
        List<Long> seqList = new ArrayList<>();
        try {
            // given
            for (int i = 0; i < chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqList.add(seq);
                chatRoomService.writeChatHistory(chatRoom.getRoomId(),
                        ChatDto.builder()
                                .roomId(chatRoom.getRoomId())
                                .seq(seq)
                                .senderName("kim")
                                .message("hello world " + i)
                                .senderSessionId("xxx-aaa-123-adf")
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }

            // when
            List<ChatHistory> chatHistories = chatRoomService.readChatHistoryCache(chatRoom.getRoomId(), memberId);

            // then
            Assertions.assertEquals(chatHistories.size(), 100);
            for (int i = 400; i < chatHistoryNumber; i++) {
                Assertions.assertEquals(chatHistories.get(i - 400).getSeq(), seqList.get(i));
            }
        }
        finally {
            for(int i=0; i<chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(seqList.get(i));
            }
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(chatRoom.getRoomId()));
            bucket.delete();
        }
    }

    @Test
    void chatroom_채팅_history를_cache에서_읽을_수_있으나_입장시간_이후_데이터만_읽는다() {
        String roomName = "room16";
        Long memberId = 18L;
        Long newMemberId = 19L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        int chatHistoryNumber = 500;
        List<Long> seqList = new ArrayList<>();
        try {
            // given
            int target = 50;
            for(int i=0; i<chatHistoryNumber-target; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqList.add(seq);
                chatRoomService.writeChatHistory(chatRoom.getRoomId(),
                        ChatDto.builder()
                                .roomId(chatRoom.getRoomId())
                                .seq(seq)
                                .senderName("kim")
                                .message("hello world " + i)
                                .senderSessionId("xxx-aaa-123-adf")
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }
            MemberChatRoom enter = chatRoomService.enter(newMemberId, chatRoom.getRoomId());
            for(int i=chatHistoryNumber-target; i<chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqList.add(seq);
                chatRoomService.writeChatHistory(chatRoom.getRoomId(),
                        ChatDto.builder()
                                .roomId(chatRoom.getRoomId())
                                .seq(seq)
                                .senderName("kim")
                                .message("hello world " + i)
                                .senderSessionId("xxx-aaa-123-adf")
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }

            // when
            List<ChatHistory> chatHistoriesCache = chatRoomService.readChatHistoryCache(chatRoom.getRoomId(), newMemberId);

            // then
            Assertions.assertEquals(chatHistoriesCache.size(), 50);
        } finally {
            for(int i=0; i<chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(seqList.get(i));
            }
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(chatRoom.getRoomId()));
            bucket.delete();
        }
    }

    @Test
    void chatroom_채팅_history를_cache와_페이징을_합쳐서_모두_읽을_수_있다() {
        String roomName = "room17";
        Long memberId = 20L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        int chatHistoryNumber = 500;
        int pageSize = 130;
        List<Long> seqList = new ArrayList<>();
        try {
            // given
            for (int i = 0; i < chatHistoryNumber; i++) {
                long seq = chatIdGenerateUtils.nextId();
                seqList.add(seq);
                chatRoomService.writeChatHistory(chatRoom.getRoomId(),
                        ChatDto.builder()
                                .roomId(chatRoom.getRoomId())
                                .seq(seq)
                                .senderName("kim")
                                .message("hello world " + i)
                                .senderSessionId("xxx-aaa-123-adf")
                                .createdAt(LocalDateTime.now())
                                .build()
                );
            }

            // when
            List<ChatHistory> chatHistoriesFromEs = chatRoomService.readChatHistory(chatRoom.getRoomId(), memberId);
            List<ChatHistory> chatHistoriesFromCache = chatRoomService.readChatHistoryCache(chatRoom.getRoomId(), memberId);
            Deque<ChatHistory> chatHistoriesFromCacheAndPagedEs = new LinkedList<>();
            chatHistoriesFromCache.forEach(history -> {
                chatHistoriesFromCacheAndPagedEs.addLast(history);
            });
            int remains = chatHistoryNumber - chatHistoriesFromCache.size();
            long currentSeq = chatHistoriesFromCache.get(0).getSeq();
            while(remains > 0) {
                List<ChatHistory> chatHistories = chatRoomService.readChatHistory(chatRoom.getRoomId(), memberId, currentSeq, pageSize);
                currentSeq = chatHistories.get(0).getSeq();
                remains -= chatHistories.size();
                for(int i=chatHistories.size()-1; i>=0; i--) {
                    ChatHistory chatHistory = chatHistories.get(i);
                    chatHistoriesFromCacheAndPagedEs.addFirst(chatHistory);
                }
            }

            // then
            for(int i=0; i<chatHistoryNumber; i++) {
                ChatHistory chatHistoryFromEs = chatHistoriesFromEs.get(i);
                ChatHistory chatHistory = chatHistoriesFromCacheAndPagedEs.pollFirst();
                boolean isEquals = chatHistory.equals(chatHistoryFromEs);
                Assertions.assertTrue(isEquals);
            }
        } finally {
            for(int i=0; i<chatHistoryNumber; i++) {
                chatHistoryRepository.deleteBySeq(seqList.get(i));
            }
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(chatRoom.getRoomId()));
            bucket.delete();
        }
    }
}
