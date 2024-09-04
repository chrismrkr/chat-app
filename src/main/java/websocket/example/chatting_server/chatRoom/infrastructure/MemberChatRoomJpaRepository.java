package websocket.example.chatting_server.chatRoom.infrastructure;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.util.List;

public interface MemberChatRoomJpaRepository extends JpaRepository<MemberChatRoomEntity, MemberChatRoomId> {
    List<MemberChatRoomEntity> findByMemberId(Long memberId);
    @Query(value = "SELECT mcr " +
            "FROM MemberChatRoomEntity mcr " +
            "JOIN FETCH mcr.chatRoomEntity " +
            "WHERE mcr.memberId = :memberId"
    )
    List<MemberChatRoomEntity> findByMemberIdWithChatRoom(@Param("memberId") Long memberId);
    @Query(value = "SELECT mcr " +
                    "FROM MemberChatRoomEntity mcr " +
                    "WHERE mcr.chatRoomEntity.roomId = :roomId"
            )
    List<MemberChatRoomEntity> findByRoomId(@Param("roomId") Long roomId);
    @Query(value = "SELECT mcr " +
                    "FROM MemberChatRoomEntity mcr " +
                    "JOIN FETCH mcr.chatRoomEntity " +
                    "WHERE mcr.chatRoomEntity.roomId = :roomId")
    List<MemberChatRoomEntity> findByRoomIdWithChatRoom(@Param("roomId") Long roomId);
}
