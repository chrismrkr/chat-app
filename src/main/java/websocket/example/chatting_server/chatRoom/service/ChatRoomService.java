package websocket.example.chatting_server.chatRoom.service;

import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface ChatRoomService {
    void delete(Long roomId);
    List<ChatRoom> findAll();
    List<ChatRoom> findByMemberId(Long memberId);
    ChatRoom create(Long memberId, String roomName);
    MemberChatRoom enter(Long memberId, Long roomId);
    void exit(Long memberId, Long roomId);
    ChatHistory writeChatHistory(Long roomId, ChatDto chatDto);
    List<ChatHistory> readChatHistory(Long roomId, Long memberId);
    List<ChatHistory> readChatHistory(Long roomId, Long memberId, long currentSeq, int size);
    List<ChatHistory> readChatHistoryCache(Long roomId, Long memberId);
}
