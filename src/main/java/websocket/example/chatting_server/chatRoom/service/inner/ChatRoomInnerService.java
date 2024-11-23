package websocket.example.chatting_server.chatRoom.service.inner;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

public interface ChatRoomInnerService {
    ChatRoom exit(Long memberId, Long roomId);
    void checkEmpty(Long roomId);
}
