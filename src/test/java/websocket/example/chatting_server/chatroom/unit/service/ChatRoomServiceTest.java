package websocket.example.chatting_server.chatroom.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chatRoom.controller.port.ChatRoomService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.service.ChatRoomServiceImpl;
import websocket.example.chatting_server.chatRoom.service.port.ChatRoomRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockChatRoomRepository;

import java.util.List;

public class ChatRoomServiceTest {
    ChatRoomRepository chatRoomRepository = new MockChatRoomRepository();
    ChatRoomService chatRoomService = new ChatRoomServiceImpl(chatRoomRepository);
    @Test
    void chatRoom_생성() {
        // given
        String roomName1 = "room1";
        Long memberId1 = 1L;
        // when
        ChatRoom chatRoom = chatRoomService.create(memberId1, roomName1);
        // then
        Assertions.assertEquals(roomName1, chatRoom.getRoomName());
        Assertions.assertNotNull(chatRoom.getRoomId());
    }

    @Test
    void chatRoom_삭제() {
        // given
        String roomName2 = "room2";
        Long memberId2 = 2L;
        ChatRoom chatRoom = chatRoomService.create(memberId2, roomName2);
        // when
        chatRoomService.delete(chatRoom.getRoomId());
        // then
        List<ChatRoom> all = chatRoomService.findAll();
        Assertions.assertEquals(0, all.size());
    }

    @Test
    void chatRoom_전체조회() {
        // given
        String roomName3 = "room3";
        Long memberId3 = 3L;
        ChatRoom chatRoom1 = chatRoomService.create(memberId3, roomName3);
        String roomName4 = "room4";
        Long memberId4 = 4L;
        ChatRoom chatRoom2 = chatRoomService.create(memberId4, roomName4);
        // when
        List<ChatRoom> all = chatRoomService.findAll();
        // then
        Assertions.assertEquals(2, all.size());
    }
}
