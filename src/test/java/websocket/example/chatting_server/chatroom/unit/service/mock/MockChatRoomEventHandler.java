package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;

public class MockChatRoomEventHandler implements ChatRoomEventHandler {

    @Override
    public void publishEmptyCheck(Long roomId) {

    }

    @Override
    public void subscribeEmptyCheck(Long roomId) {

    }
}
