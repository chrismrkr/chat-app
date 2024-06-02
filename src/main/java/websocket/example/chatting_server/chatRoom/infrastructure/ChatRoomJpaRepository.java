package websocket.example.chatting_server.chatRoom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Long> {

}
