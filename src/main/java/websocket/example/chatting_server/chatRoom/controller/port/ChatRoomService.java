package websocket.example.chatting_server.chatRoom.controller.port;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    void delete(Long roomId);
    List<ChatRoom> findAll();
    ChatRoom create(Long memberId, String roomName);

}
