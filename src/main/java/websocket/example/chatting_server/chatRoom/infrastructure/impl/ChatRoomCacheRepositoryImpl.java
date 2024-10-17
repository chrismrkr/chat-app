package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCacheRepositoryImpl implements ChatRoomCacheRepository {
    private final RedisTemplate redisTemplate;
    @Override
    public boolean lockChatRoomHistory(String roomId) {
        return false;
    }

    @Override
    public boolean unlockChatRoomHistory(String roomId) {
        return false;
    }
}