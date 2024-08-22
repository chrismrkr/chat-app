package websocket.example.chatting_server.chatroom.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.service.ChatHistoryService;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.service.impl.ChatRoomServiceImpl;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockChatHistoryRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockChatRoomRepository;
import websocket.example.chatting_server.chatroom.unit.service.mock.MockMemberChatRoomRepository;

import java.util.List;
import java.util.Optional;

public class ChatRoomServiceTest {
    ChatRoomRepository chatRoomRepository = new MockChatRoomRepository();
    MemberChatRoomRepository memberChatRoomRepository = new MockMemberChatRoomRepository();
    ChatHistoryRepository chatHistoryRepository = new MockChatHistoryRepository();
    ChatRoomService chatRoomService = new ChatRoomServiceImpl(chatRoomRepository, memberChatRoomRepository, chatHistoryRepository);
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

    @Test
    void chatroom에_사용자가_입장() {
        // given
        String roomName = "room4";
        Long memberId = 5L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);

        // when
        Long newMemberId = 6L;
        MemberChatRoom enter = chatRoomService.enter(newMemberId, chatRoom.getRoomId());

        // then
        Optional<MemberChatRoom> byMemberAndRoomId = memberChatRoomRepository.findByMemberAndRoomId(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(byMemberAndRoomId.get().getMemberId(), newMemberId);
        Assertions.assertEquals(byMemberAndRoomId.get().getChatRoom().getRoomId(), chatRoom.getRoomId());
    }

    @Test
    void 사용자의_chatroom_퇴장() {
        // given
        String roomName = "room5";
        Long memberId = 7L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        Long newMemberId = 8L;
        MemberChatRoom enter = chatRoomService.enter(newMemberId, chatRoom.getRoomId());

        // when
        chatRoomService.exit(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(memberChatRoomRepository.findByMemberId(memberId).size(), 1);
        Assertions.assertEquals(memberChatRoomRepository.findByMemberId(newMemberId).size(), 0);
        Assertions.assertNotNull(chatRoomRepository.findById(chatRoom.getRoomId()));
    }

    @Test
    void 사용자_chatroom_퇴장_후_남은_인원이_없으면_채팅방을_삭제함() {
        // given
        String roomName = "room6";
        Long memberId = 9L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        // when
        chatRoomService.exit(memberId, chatRoom.getRoomId());
        // then
        Assertions.assertEquals(memberChatRoomRepository.findByMemberId(memberId).size() ,0);
        Assertions.assertEquals(chatRoomRepository.findById(chatRoom.getRoomId()), Optional.empty());
    }
}
