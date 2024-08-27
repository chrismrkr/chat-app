package websocket.example.chatting_server.chatRoom.infrastructure;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    Optional<ChatRoom> findByIdWithParticipants(Long roomId);
    Optional<ChatRoom> findById(Long roomId);
    List<ChatRoom> findAll();
    ChatRoom create(String roomName);
    void delete(ChatRoom chatRoom);
    void delete(Long roomId);
}
