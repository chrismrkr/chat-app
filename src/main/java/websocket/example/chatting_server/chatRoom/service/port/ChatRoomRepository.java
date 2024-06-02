package websocket.example.chatting_server.chatRoom.service.port;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    Optional<ChatRoom> findById(Long roomId);
    List<ChatRoom> findAll();
    ChatRoom create(Long memberId, String roomName);
    void delete(ChatRoom chatRoom);
}
