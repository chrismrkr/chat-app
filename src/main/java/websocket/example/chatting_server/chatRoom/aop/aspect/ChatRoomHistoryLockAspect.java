package websocket.example.chatting_server.chatRoom.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;
import websocket.example.chatting_server.chatRoom.aop.annotation.ChatRoomHistoryLock;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomHistoryLockAspect {
    private final ChatRoomCacheRepository chatRoomCacheRepository;
    private static final Long LOCK_TIME_OUT_MS = 100L; // 100ms
    private static final Long TTL_MS = 5 * 1000L; // 5sec
    @Around("@annotation(websocket.example.chatting_server.chatRoom.aop.annotation.ChatRoomHistoryLock) && args(roomId, ..)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, Long roomId) throws Throwable {
        RLock chatRoomHistoryLock = chatRoomCacheRepository.getChatRoomHistoryLock(roomId);
        try {
            if(chatRoomHistoryLock.tryLock(LOCK_TIME_OUT_MS, TTL_MS, TimeUnit.MILLISECONDS)) {
                try {
                    return joinPoint.proceed();
                } finally {
                    chatRoomHistoryLock.unlock();
                }
            }
            else {
                throw new InterruptedException("CHATROOM HISTORY LOCK FAILED");
            }
        } catch (InterruptedException e) {
            throw e;
        }
    }
}
