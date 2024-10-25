package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCacheRepositoryImpl implements ChatRoomCacheRepository {
    private final RedissonClient redissonClient;
    private static final String CHAT_ROOM_HISTORY_LOCK_PREFIX = "CHAT_ROOM_HISTORY_LOCK_";
    private static final String CHAT_ROOM_HISTORY_CACHE_PREFIX = "CHAT_ROOM_HISTORY_CACHE_";
    private static final int MAX_CACHE_SIZE = 100;
    @Override
    public RLock getChatRoomHistoryLock(Long roomId) {
        String key = CHAT_ROOM_HISTORY_LOCK_PREFIX + Long.toString(roomId);
        return redissonClient.getLock(key);
    }

    @Override
    public ChatHistory writeChatHistory(Long roomId, ChatHistory chatHistory) {
        String key = CHAT_ROOM_HISTORY_CACHE_PREFIX + Long.toString(roomId);
        RDeque<ChatHistory> chatHistoryCache = redissonClient.getDeque(key);
        chatHistoryCache.addFirst(chatHistory);
        if(chatHistoryCache.size() > MAX_CACHE_SIZE) {
            chatHistoryCache.removeLast();
        }
        return chatHistory;
    }

    @Override
    public List<ChatHistory> readChatHistory(Long roomId) {
        String key = CHAT_ROOM_HISTORY_CACHE_PREFIX + Long.toString(roomId);
        RDeque<ChatHistory> deque = redissonClient.getDeque(key);
        return deque.stream().toList();
    }
}