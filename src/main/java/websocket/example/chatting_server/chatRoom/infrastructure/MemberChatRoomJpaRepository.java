package websocket.example.chatting_server.chatRoom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.util.List;

public interface MemberChatRoomJpaRepository extends JpaRepository<MemberChatRoomEntity, MemberChatRoomId> {
    List<MemberChatRoomEntity> findByMemberId(Long memberId);
    @Query(value = "SELECT mcr " +
                    "FROM MemberChatRoomEntity mcr " +
                    "WHERE mcr.chatRoomEntity.roomId = :roomId"
            )
    List<MemberChatRoomEntity> findByRoomId(@Param("roomId") Long roomId);
}
