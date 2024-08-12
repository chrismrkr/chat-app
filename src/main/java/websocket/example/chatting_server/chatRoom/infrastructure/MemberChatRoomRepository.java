package websocket.example.chatting_server.chatRoom.infrastructure;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;

import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepository {
    MemberChatRoom addMemberInChatRoom(Long memberId, ChatRoom chatRoom);
    void deleteMemberChatroomMapping(MemberChatRoom memberChatRoom);
    List<MemberChatRoom> findByMemberId(Long memberId);
    List<MemberChatRoom> findByRoomId(Long roomId);
    Optional<MemberChatRoom> findByMemberAndRoomId(Long memberId, Long roomId);
}