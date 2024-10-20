package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDeque;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCacheRepositoryImpl implements ChatRoomCacheRepository {
    private final RedissonClient redissonClient;
    private static final String CHAT_ROOM_HISTORY_LOCK_PREFIX = "CHAT_ROOM_HISTORY_LOCK_";
    private static final String CHAT_ROOM_HISTORY_CACHE_PREFIX = "CHAT_ROOM_HISTORY_CACHE_";
    private static final int MAX_CACHE_SIZE = 200;
    @Override
    public RLock getChatRoomHistoryLock(Long roomId) {
        String key = CHAT_ROOM_HISTORY_LOCK_PREFIX + Long.toString(roomId);
        return redissonClient.getLock(key);
    }

    @Override
    public ChatHistory write(Long roomId, ChatHistory chatHistory) {
        String key = CHAT_ROOM_HISTORY_CACHE_PREFIX + Long.toString(roomId);
        RDeque<Object> chatHistoryCache = redissonClient.getDeque(key);
        chatHistoryCache.addFirst(chatHistory);
        if(chatHistoryCache.size() > MAX_CACHE_SIZE) {
            chatHistoryCache.removeLast();
        }
        return chatHistory;
    }
}