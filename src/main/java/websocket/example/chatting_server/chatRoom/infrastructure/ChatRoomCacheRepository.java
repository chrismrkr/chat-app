package websocket.example.chatting_server.chatRoom.infrastructure;

import org.redisson.api.RLock;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;

public interface ChatRoomCacheRepository {
    RLock getChatRoomHistoryLock(Long roomId);
    ChatHistory write(Long roomId, ChatHistory chatHistory);
}
