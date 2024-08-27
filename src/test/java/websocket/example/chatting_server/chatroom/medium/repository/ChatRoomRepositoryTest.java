package websocket.example.chatting_server.chatroom.medium.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ChatRoomRepositoryTest {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    EntityManager em;
    @Autowired
    public ChatRoomRepositoryTest(ChatRoomRepository chatRoomRepository, MemberChatRoomRepository memberChatRoomRepository, EntityManager em) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberChatRoomRepository = memberChatRoomRepository;
        this.em = em;
    }

    @Test
    @Transactional
    void Member가_ChatRoom을_생성() {
        // given
        String roomName = "room1";
        Long memberId = 1L;
        // when
        ChatRoom chatRoom = chatRoomRepository.create(roomName);
        MemberChatRoom memberChatRoom = memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        em.flush();
        em.clear();

        // then
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        Assertions.assertEquals(roomName, byId.get().getRoomName());
        Assertions.assertNotNull(byId.get().getRoomId());
        Assertions.assertEquals(byId.get().getRoomId(), chatRoom.getRoomId());

        List<MemberChatRoom> byMemberId = memberChatRoomRepository.findByMemberId(memberId);
        Assertions.assertEquals(1, byMemberId.size());
        Assertions.assertEquals(memberId, byMemberId.get(0).getMemberId());
        Assertions.assertEquals(chatRoom.getRoomId(), byMemberId.get(0).getChatRoom().getRoomId());
        Assertions.assertNotNull(byMemberId.get(0).getEnterDateTime());
    }

    @Test
    @Transactional
    void Chatroom을_roomId로_검색() {
        // given
        String roomName = "room2";
        Long memberId2 = 2L;
        ChatRoom chatRoom = chatRoomRepository.create(roomName);
        // when
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        // then
        Assertions.assertNotEquals(byId, Optional.empty());
        Assertions.assertEquals(chatRoom.getRoomName(), byId.get().getRoomName());
        Assertions.assertEquals(chatRoom.getRoomId(), byId.get().getRoomId());
    }

    @Test
    void ChatRoom_전체_검색() {
        // given
        Long memberId3 = 3L;
        String roomName3 = "room3";
        String roomName4 = "room4";
        chatRoomRepository.create(roomName3);
        chatRoomRepository.create(roomName4);
        // when
        List<ChatRoom> all = chatRoomRepository.findAll();
        // then
        Assertions.assertTrue(() -> all.size() >= 2);
    }
    @Test
    void ChatRoom을_roomId로_삭제() {
        // given
        String roomName5 = "room5";
        Long memberId4 = 4L;
        ChatRoom chatRoom = chatRoomRepository.create(roomName5);
        // when
        chatRoomRepository.delete(chatRoom);
        // then
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        Assertions.assertEquals(byId, Optional.empty());
    }

    @Test
    @Transactional
    void MemberChatRoom을_memberId로_검색() {
        // given
        String[] roomNames = {"room6", "room7"};
        Long memberId = 6L;
        for(String roomName : roomNames) {
            ChatRoom chatRoom = chatRoomRepository.create(roomName);
            memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        }
        em.flush();
        em.clear();

        // when
        List<MemberChatRoom> byMemberId = memberChatRoomRepository.findByMemberId(memberId);

        // then
        Assertions.assertEquals(2, byMemberId.size());
        for(int i=0; i<2; i++) {
            Assertions.assertEquals(byMemberId.get(i).getChatRoom().getRoomName(), roomNames[i]);
            Assertions.assertNotNull(byMemberId.get(i).getChatRoom().getRoomId());
        }
    }

    // orphanremoval과 cascade remove 차이 정리할 것
    @Test
    void chatroom이_삭제되면_memberChatroom_연관관계_매핑도_제거() {
        // given
        String roomName = "room8";
        Long[] memberIds = {7L, 8L, 9L};
        ChatRoom chatRoom = joinChatRoom(roomName, memberIds);
        // when
        chatRoomRepository.delete(chatRoom);

        // then
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        Assertions.assertEquals(byId, Optional.empty());
        for(Long memberId : memberIds) {
            Assertions.assertEquals(0, memberChatRoomRepository.findByMemberId(memberId).size());
        }
    }

    @Test
    void memberChatRoom을_memberId와_roomId로_조회() {
        // given
        String roomName = "room9";
        Long[] memberIds = {10L};
        ChatRoom chatRoom = joinChatRoom(roomName, memberIds);

        // when
        Optional<MemberChatRoom> byMemberAndRoomId = memberChatRoomRepository
                .findByMemberAndRoomId(memberIds[0], chatRoom.getRoomId());

        // then
        Assertions.assertNotEquals(byMemberAndRoomId, Optional.empty());
    }

    @Test
    void memberChatroom을_삭제() {
        // given
        String roomName = "room10";
        Long[] memberIds = {11L, 12L};
        ChatRoom chatRoom = joinChatRoom(roomName, memberIds);

        // when
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndRoomId(memberIds[0], chatRoom.getRoomId())
                .get();
        memberChatRoomRepository.deleteMemberChatroomMapping(memberChatRoom);

        // then
        Assertions.assertEquals(
                memberChatRoomRepository.findByMemberId(11L).size(),
                0);
    }

    @Test
    void MemberChatRoom을_roomId로_조회() {
        // given
        String roomName = "room11";
        Long[] memberIds = {13L, 14L};
        ChatRoom chatRoom = joinChatRoom(roomName, memberIds);

        // when
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(chatRoom.getRoomId());

        // then
        Assertions.assertEquals(2, byRoomId.size());
        for(int i=0; i<memberIds.length; i++) {
            Assertions.assertEquals(byRoomId.get(i).getMemberId(), memberIds[i]);
            Assertions.assertEquals(byRoomId.get(i).getChatRoom().getRoomId(), chatRoom.getRoomId());
        }
    }

    @Test
    void Chatroom을_roomId로_삭제() {
        // given
        String roomName = "room12";
        ChatRoom chatRoom = chatRoomRepository.create(roomName);

        // when
        chatRoomRepository.delete(chatRoom.getRoomId());

        // then
        Assertions.assertEquals(
        chatRoomRepository.findById(chatRoom.getRoomId())
        , Optional.empty());
    }
    @Test
    void 동일한_memberChatRoom을_2번_저장해도_최초_생성일이_변경되지_않음() throws InterruptedException {
        // given
        String roomName = "chatroomrepotest-room13";
        ChatRoom chatRoom = chatRoomRepository.create(roomName);
        Long memberId = 15L;

        // when
        MemberChatRoom memberChatRoom1 = memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        Thread.sleep(600);
        MemberChatRoom memberChatRoom2 = memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        Thread.sleep(600);

        // then
        MemberChatRoom find = memberChatRoomRepository
                                .findByMemberAndRoomId(memberId, chatRoom.getRoomId())
                                .get();
        Assertions.assertEquals(find.getEnterDateTime().getSecond(), memberChatRoom1.getEnterDateTime().getSecond());
    }
    @Transactional
    private ChatRoom joinChatRoom(String roomName, Long[] memberIds) {
        ChatRoom chatRoom = chatRoomRepository.create(roomName);
        for(Long memberId : memberIds) {
            memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        }
        return chatRoom;
    }
}
