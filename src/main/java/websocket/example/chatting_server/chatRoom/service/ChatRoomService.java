package websocket.example.chatting_server.chatRoom.service;

import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    void delete(Long roomId);
    List<ChatRoom> findAll();
    List<ChatRoom> findByMemberId(Long memberId);
    ChatRoom create(Long memberId, String roomName);
    MemberChatRoom enter(Long memberId, Long roomId);
    void exit(Long memberId, Long roomId);
    List<ChatHistory> readChatHistory(Long memberId, Long roomId);
}
