package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.ShiftRight;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class ChatRoomCacheRepositoryImpl implements ChatRoomCacheRepository {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, ChatHistory> chatHistoryRedisTemplate;
    private static final String CHAT_ROOM_HISTORY_LOCK_PREFIX = "CHAT_ROOM_HISTORY_LOCK_";
    private static final String CHAT_ROOM_HISTORY_CACHE_PREFIX = "CHAT_ROOM_HISTORY_CACHE_";
    private static final int MAX_CACHE_SIZE = 100;

    @Autowired
    public ChatRoomCacheRepositoryImpl(RedissonClient redissonClient, @Qualifier("chatHistoryRedisTemplate") RedisTemplate<String, ChatHistory> redisTemplate) {
        this.redissonClient = redissonClient;
        this.chatHistoryRedisTemplate = redisTemplate;
    }

    @Override
    public RLock getChatRoomHistoryLock(Long roomId) {
        String key = CHAT_ROOM_HISTORY_LOCK_PREFIX + Long.toString(roomId);
        return redissonClient.getLock(key);
    }

    @Override
    public ChatHistory writeChatHistory(Long roomId, ChatHistory chatHistory) {
        String key = CHAT_ROOM_HISTORY_CACHE_PREFIX + Long.toString(roomId);
        chatHistoryRedisTemplate.opsForList().leftPush(key, chatHistory);
        chatHistoryRedisTemplate.opsForList().trim(key, 0, MAX_CACHE_SIZE-1);
        return chatHistory;
    }

    @Override
    public List<ChatHistory> readChatHistory(Long roomId) {
        String key = CHAT_ROOM_HISTORY_CACHE_PREFIX + Long.toString(roomId);
        List<ChatHistory> histories = chatHistoryRedisTemplate.opsForList().range(key, 0, -1);
        Collections.reverse(Objects.requireNonNull(histories));
        return histories;
    }
}