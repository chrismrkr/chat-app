package websocket.example.chatting_server.chat.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chat.infrastructure.LockRepository;

import java.time.Duration;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisLockRepositoryImpl implements LockRepository {
    private final RedisTemplate redisTemplate;

    @Value("${spring.data.redis.lock.duration-millis}")
    private String lockDurationMillis;

    @Override
    public boolean holdLock(String key, String value) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean ret = valueOperations.setIfAbsent(key, value,
                Duration.ofMillis(Long.parseLong(lockDurationMillis)));
        return ret;
    }

    @Override
    public void releaseLock(String key) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(key);
        return;
    }

    @Override
    public boolean isLocked(String key) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object o = valueOperations.get(key);
        return o != null ? true : false;
    }
}
