package websocket.example.chatting_server.chatroom.unit.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ChatRoomDomainTest {
    @Test
    void ChatRoom을_ChatRoomEntity로_변환() {
        // given
        ChatRoom room1 = ChatRoom.builder()
                .roomName("room1")
                .roomId(1L)
                .build();
        // when
        ChatRoomEntity entity = room1.toEntity();
        // then
        Assertions.assertEquals(room1.getRoomId(), entity.getRoomId());
        Assertions.assertEquals(room1.getRoomName(), entity.getRoomName());
    }
    @Test
    void MemberChatRoomEntity가_없는_ChatRoomEntity를_ChatRoom으로_변환() {
        // given
        ChatRoomEntity room2 = ChatRoomEntity.builder()
                .roomId(2L)
                .roomName("room2")
                .build();
        // when
        ChatRoom from = ChatRoom.from(room2);
        // then
        Assertions.assertEquals(room2.getRoomId(), from.getRoomId());
        Assertions.assertEquals(room2.getRoomName(), from.getRoomName());
        Assertions.assertTrue(room2.getMemberChatRoomEntities().isEmpty());
    }
    @Test
    void MemberChatRoomEntity를_포함한_ChatRoomEntity를_ChatRoom으로_변환() {
        // given
        Long memberId = 1L;
        Long roomId = 3L;
        String roomName = "room3";
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .roomId(roomId)
                .roomName(roomName)
                .build();
        chatRoomEntity.getMemberChatRoomEntities()
                .add(MemberChatRoomEntity.builder()
                        .memberId(memberId)
                        .chatRoomEntity(chatRoomEntity)
                        .enterDateTime(LocalDateTime.now())
                        .build());

        // when
        ChatRoom from = ChatRoom.from(chatRoomEntity);

        // then
        Assertions.assertEquals(from.getParticipants().size(), 1);
        Assertions.assertEquals(from.getRoomId(), roomId);
        Assertions.assertEquals(from.getRoomName(), roomName);
    }

    @Test
    void Chatroom에_Member가_참여할_수_있다() {
        // given
        Long roomId = 4L;
        Long memberId = 1L;
        String roomName = "room4";
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomName(roomName)
                .build();

        // when
        MemberChatRoom participate = chatRoom.participate(memberId);

        // then
        Assertions.assertEquals(chatRoom.getParticipants().size(), 1);
        Assertions.assertEquals(participate.getMemberId(), memberId);
        Assertions.assertEquals(participate.getChatRoom().getRoomId(), roomId);

    }

    @Test
    void 특정_Member가_현재_채팅방에_참여_중인지_확인() {
        // given
        Long roomId = 5L;
        Long memberId = 1L;
        String roomName = "room5";
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomName(roomName)
                .build();
        chatRoom.participate(memberId);
        // when
        MemberChatRoom participants = chatRoom.findParticipants(memberId).get();
        // then
        Assertions.assertEquals(participants.getMemberId(), memberId);
    }
 }
