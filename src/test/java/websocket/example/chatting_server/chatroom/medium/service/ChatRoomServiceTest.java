package websocket.example.chatting_server.chatroom.medium.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.service.ChatHistoryService;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Rollback
public class ChatRoomServiceTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;
    @Autowired
    ChatHistoryService chatHistoryService;
    @Autowired
    ChatHistoryRepository chatHistoryRepository;
    @Autowired
    ChatIdGenerateUtils chatIdGenerateUtils;

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

    @Test
    void memberId로_chatroom_조회() {
        // given
        Long memberId1 = 8L;
        Long memberId2 = 9L;
        String[] roomNames = {"room7", "room8", "room9"};
        chatRoomService.create(memberId1, roomNames[0]);
        chatRoomService.create(memberId1, roomNames[1]);
        chatRoomService.create(memberId2, roomNames[2]);

        // when
        List<ChatRoom> byMemberId1 = chatRoomService.findByMemberId(memberId1);
        List<ChatRoom> byMemberId2 = chatRoomService.findByMemberId(memberId2);

        // then
        Assertions.assertEquals(byMemberId1.size(), 2);
        Assertions.assertEquals(byMemberId2.size(), 1);
        Assertions.assertEquals(byMemberId2.get(0).getRoomName(), roomNames[2]);
    }

    @Test
    void chatRoom의_History를_RoomId_및_입장시간을_조건으로_조회() throws InterruptedException {
        // given
        Long memberId = 12L;
        Long newMemberId = 13L;
        String roomName = "room12";
        ChatRoom chatRoom = chatRoomService.create(memberId, roomName);
        List<Long> seqList = new ArrayList<>();

        for(int i=0; i<10; i++) {
            long seq = chatIdGenerateUtils.nextId();
            ChatHistory write = chatHistoryService.write(
                    ChatDto.builder()
                            .seq(seq)
                            .roomId(chatRoom.getRoomId())
                            .senderName("member")
                            .message("hello")
                            .build()
            );
            seqList.add(seq);
        }

        // when
        Thread.sleep(100);
        chatRoomService.enter(newMemberId, chatRoom.getRoomId());
        Thread.sleep(100);
        for(int i=0; i<100; i++) {
            long seq = chatIdGenerateUtils.nextId();
            chatHistoryService.write(
                    ChatDto.builder()
                            .seq(seq)
                            .roomId(chatRoom.getRoomId())
                            .senderName("member")
                            .message("hello")
                            .build()
            );
            seqList.add(seq);
        }

        // then
        List<ChatHistory> chatHistories = chatRoomService.readChatHistory(newMemberId, chatRoom.getRoomId());
        Assertions.assertEquals(chatHistories.size(), 100);
        for(Long seq : seqList) {
            chatHistoryRepository.deleteBySeq(seq);
        }
    }
}
