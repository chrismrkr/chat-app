package websocket.example.chatting_server.chatroom.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.service.impl.ChatRoomServiceImpl;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockChatRoomRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockMemberChatRoomRepository;

import java.util.List;

public class ChatRoomServiceTest {
    ChatRoomRepository chatRoomRepository = new MockChatRoomRepository();
    MemberChatRoomRepository memberChatRoomRepository = new MockMemberChatRoomRepository();
    ChatRoomService chatRoomService = new ChatRoomServiceImpl(chatRoomRepository, memberChatRoomRepository);
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
        List<MemberChatRoom> byMemberId = memberChatRoomRepository.findByMemberId(memberId1);
        Assertions.assertEquals(1, byMemberId.size());
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
        List<MemberChatRoom> byMemberId = memberChatRoomRepository.findByMemberId(memberId2);
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
