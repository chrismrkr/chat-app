package websocket.example.chatting_server.chatroom.medium.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ChatRoomCacheRepositoryTest {
    @Autowired
    ChatRoomCacheRepository chatRoomCacheRepository;
    @Autowired
    RedissonClient redissonClient;

    @Test
    void CHAT_ROOM에_분산락_획득_및_해제_가능() throws InterruptedException {
        // given
        Long roomId = 1L;
        // when
        long lockTimeOutMs = 100L;
        long TTL = 2 * 1000L;
        boolean isLocked = false;
        RLock chatRoomHistoryLock = chatRoomCacheRepository.getChatRoomHistoryLock(roomId);
        try {
            if (chatRoomHistoryLock.tryLock(lockTimeOutMs, TTL, TimeUnit.MILLISECONDS)) {
                // lockTimeOut(ms) 동안 Lock 획득을 시도하고, TTL은 TTL(ms)임
                isLocked = true;
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            chatRoomHistoryLock.unlock();
        }
        // then
        Assertions.assertEquals(isLocked, true);
    }
    @Test
    void CHAT_ROOM_분산락은_TTL_뒤에_자동으로_해제됨() throws InterruptedException {
        // given
        Long roomId = 2L;
        // when
        long lockTimeOutMs = 100L;
        long TTL = 2 * 1000L;
        long wait = 2500L;
        RLock chatRoomHistoryLock = chatRoomCacheRepository.getChatRoomHistoryLock(roomId);
        try {
            if (chatRoomHistoryLock.tryLock(lockTimeOutMs, TTL, TimeUnit.MILLISECONDS)) {
                // lockTimeOut(ms) 동안 Lock 획득을 시도하고, TTL은 TTL(ms)임
                Thread.sleep(wait);
            }
        } catch (InterruptedException e) {
            throw e;
        }
        // then
        Assertions.assertEquals(chatRoomHistoryLock.isLocked(), false);
    }

    @Test
    void CHAT_ROOM_분산락이_이미_걸려있으면_다른_쪽에서는_접근_불가() throws InterruptedException {
        // given
        Long roomId = 3L;
        // when
        long lockTimeOutMs = 100L;
        long TTL = 2 * 1000L;
        AtomicInteger count = new AtomicInteger(0);

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for(long i=0; i<threadCount; i++) {
            executorService.execute(() -> {
                System.out.println("CURRENT COUNT : " + count.get()+1);
                RLock chatRoomHistoryLock = chatRoomCacheRepository.getChatRoomHistoryLock(roomId);
                try {
                    if(chatRoomHistoryLock.tryLock(lockTimeOutMs, TTL, TimeUnit.MILLISECONDS)) {
                        count.getAndIncrement();
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException ignored) {
                    System.out.println("ERROR!");
                } finally {
                    countDownLatch.countDown();
                    chatRoomHistoryLock.unlock();
                }
            });
        }
        countDownLatch.await();

        // then
        Assertions.assertEquals(count.get(), 1);
    }

    @Test
    void ChatHistory를_Cache에_기록할_수_있음() {
        Long roomId = 4L;
        try {
            // given
            ChatHistory chatHistory = ChatHistory.builder()
                    .roomId(roomId)
                    .seq(1L)
                    .senderName("kim")
                    .message("hello1")
                    .sendTime(LocalDateTime.now())
                    .build();
            // when
            ChatHistory chatHistory1 = chatRoomCacheRepository.writeChatHistory(roomId, chatHistory);
            // then
            List<ChatHistory> chatHistories = chatRoomCacheRepository.readChatHistory(roomId);
            Assertions.assertEquals(chatHistories.size(), 1);
            Assertions.assertEquals(chatHistories.get(0).getSeq(), 1L);

        } finally {
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(roomId));
            bucket.delete();
        }
    }

    @Test
    void ChatHistory를_Cache에_기록했으나_MAX_CACHE_SIZE를_초과하면_LRU_정책으로_제거() {
        // given
        Long roomId = 5L;
        int MAX_CACHE_SIZE = 100;
        try {
            // when
            for(int i=0; i<MAX_CACHE_SIZE + 5; i++) {
                ChatHistory chatHistory = ChatHistory.builder()
                        .roomId(roomId)
                        .seq((long) i)
                        .senderName("kim")
                        .message("hello1")
                        .sendTime(LocalDateTime.now())
                        .build();
                chatRoomCacheRepository.writeChatHistory(roomId, chatHistory);
            }
            // then
            List<ChatHistory> chatHistories = chatRoomCacheRepository.readChatHistory(roomId);
            Assertions.assertEquals(chatHistories.size(), 100);
            Assertions.assertEquals(chatHistories.get(MAX_CACHE_SIZE - 1).getSeq(), 5);
        } finally {
            RBucket<Object> bucket = redissonClient.getBucket("CHAT_ROOM_HISTORY_CACHE_" + Long.toString(roomId));
            bucket.delete();
        }
    }
}
