package websocket.example.chatting_server.chatRoom.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ChatRoomHistoryLockAspect {
    @Around("@annotation(ChatRoomHistoryLock)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO. LOCK
        Object proceed = joinPoint.proceed();
        // TODO. UNLOCK
        return proceed;
    }
}
