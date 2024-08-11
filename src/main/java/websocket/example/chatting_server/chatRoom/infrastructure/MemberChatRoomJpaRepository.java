package websocket.example.chatting_server.chatRoom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.util.List;

public interface MemberChatRoomJpaRepository extends JpaRepository<MemberChatRoomEntity, MemberChatRoomId> {
    List<MemberChatRoomEntity> findByMemberId(Long memberId);
}
