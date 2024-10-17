package websocket.example.chatting_server.chatRoom.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomHistoryLockAspect {
    private final ChatRoomCacheRepository chatRoomCacheRepository;
    @Around("@annotation(ChatRoomHistoryLock)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        // TODO. LOCK
        Object proceed = joinPoint.proceed();
        // TODO. UNLOCK
        return proceed;
    }
}
