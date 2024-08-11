package websocket.example.chatting_server.chatRoom.service;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;

import java.util.List;

public interface ChatRoomService {
    void delete(Long roomId);
    List<ChatRoom> findAll();
    ChatRoom create(Long memberId, String roomName);
    MemberChatRoom enter(Long memberId, Long roomId);
    void exit(Long memberId, Long roomId);
}
