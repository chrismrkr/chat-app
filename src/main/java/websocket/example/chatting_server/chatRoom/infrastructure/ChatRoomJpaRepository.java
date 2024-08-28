package websocket.example.chatting_server.chatRoom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Long> {
    @Query(value = "SELECT c " +
                    "FROM ChatRoomEntity c " +
                    "JOIN FETCH c.memberChatRoomEntities " +
                    "WHERE c.roomId = :roomId")
    Optional<ChatRoomEntity> findByIdWithParticipants(@Param("roomId")Long roomId);
    @Query(value = "SELECT c " +
                    "FROM ChatRoomEntity c " +
                    "JOIN FETCH c.memberChatRoomEntities")
    List<ChatRoomEntity> findAllWithParticipants();
}
