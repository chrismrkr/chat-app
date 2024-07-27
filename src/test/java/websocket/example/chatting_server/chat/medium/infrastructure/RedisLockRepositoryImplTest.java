package websocket.example.chatting_server.chat.medium.infrastructure;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chat.infrastructure.impl.RedisLockRepositoryImpl;

@SpringBootTest
public class RedisLockRepositoryImplTest {
    @Autowired
    RedisLockRepositoryImpl repository;

    @Test
    void KeyValue_저장() {
        // given
        String key = "key1";
        String value = "value1";
        // when
        boolean b = repository.holdLock(key, value);
        // then
        Assertions.assertEquals(b, true);
        repository.releaseLock(key);
    }

    @Test
    void 동일한_Key로_Value를_2번_저장하면_1번째만_저장된다() {
        // given
        String key = "key2";
        String val1 = "val1";
        String val2 = "val2";
        // when
        boolean ret1 = repository.holdLock(key, val1);
        boolean ret2 = repository.holdLock(key, val2);
        // then
        Assertions.assertEquals(ret1, true);
        Assertions.assertEquals(ret2, false);
        repository.releaseLock(key);
    }

    @Test
    void KeyValue를_삭제할_수_있다() {
        // given
        String key = "key3";
        String val = "val1";
        repository.holdLock(key, val);
        // when
        repository.releaseLock(key);
        // then
        Assertions.assertEquals(repository.isLocked(key), false);
    }
}
