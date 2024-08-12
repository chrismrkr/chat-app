package websocket.example.chatting_server.chatroom.medium.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.util.Optional;

@SpringBootTest
public class ChatRoomServiceTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Test
    void chatroom_신규_생성() {
        // given
        String roomName = "room1";
        Long memberId = 1L;

        // when
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);

        // then
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByMemberId(memberId).size(),
                1
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByMemberId(memberId).get(0).getMemberId(),
                memberId
        );
    }

    @Test
    void chatroom_입장() {
        // given
        String roomName = "room2";
        Long memberId = 2L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );
        // when
        Long newMemberId = 3L;
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).size(),
                2
        );
    }

    @Test
    void chatroom_퇴장() {
        // given
        String roomName = "room3";
        Long memberId = 3L;
        Long newMemberId = 4L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );

        // when
        chatRoomService.exit(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).size(),
                1
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).get(0).getMemberId(),
                memberId
        );
        Assertions.assertEquals(
                memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).get(0).getChatRoom().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertNotNull(chatRoomRepository.findById(chatRoom.getRoomId()));
    }

    @Test
    void chatroom_퇴장_시_남은_인원이_없으면_채팅방_자동_삭제() {
        // given
        String roomName = "room4";
        Long memberId = 5L;
        Long newMemberId = 6L;
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomId(),
                chatRoom.getRoomId()
        );
        Assertions.assertEquals(
                chatRoomRepository.findById(chatRoom.getRoomId()).get().getRoomName(),
                roomName
        );

        // when
        chatRoomService.exit(memberId, chatRoom.getRoomId());
        chatRoomService.exit(newMemberId, chatRoom.getRoomId());

        // then
        Assertions.assertEquals(chatRoomRepository.findById(chatRoom.getRoomId()), Optional.empty());
        Assertions.assertEquals(memberChatRoomRepository.findByRoomId(chatRoom.getRoomId()).size(), 0);
    }
}
