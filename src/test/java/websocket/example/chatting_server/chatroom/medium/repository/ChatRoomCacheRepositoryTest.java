package websocket.example.chatting_server.chatroom.medium.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootTest

public class ChatRoomCacheRepositoryTest {
    @Autowired
    ChatRoomCacheRepository chatRoomCacheRepository;
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
        Collections.binarySearch()
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
}
