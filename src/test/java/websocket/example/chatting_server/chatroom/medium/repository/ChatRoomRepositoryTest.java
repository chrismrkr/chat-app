package websocket.example.chatting_server.chatroom.medium.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.service.port.ChatRoomRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ChatRoomRepositoryTest {
    private final ChatRoomRepository chatRoomRepository;
    @Autowired
    public ChatRoomRepositoryTest(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Test
    void ChatRoom_생성() {
        // given
        String roomName = "room1";
        Long memberId1 = 1L;
        // when
        ChatRoom chatRoom = chatRoomRepository.create(memberId1, roomName);
        // then
        Assertions.assertEquals(roomName, chatRoom.getRoomName());
        Assertions.assertNotNull(chatRoom.getRoomId());
        Assertions.assertEquals(chatRoom.getMemberId(), memberId1);
    }

    @Test
    void Chatroom_검색() {
        // given
        String roomName = "room2";
        Long memberId2 = 2L;
        ChatRoom chatRoom = chatRoomRepository.create(memberId2, roomName);
        // when
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        // then
        Assertions.assertNotEquals(byId, Optional.empty());
        Assertions.assertEquals(chatRoom.getRoomName(), byId.get().getRoomName());
        Assertions.assertEquals(chatRoom.getRoomId(), byId.get().getRoomId());
        Assertions.assertEquals(memberId2, chatRoom.getMemberId());
    }

    @Test
    void ChatRoom_전체_검색() {
        // given
        Long memberId3 = 3L;
        String roomName3 = "room3";
        String roomName4 = "room4";
        chatRoomRepository.create(memberId3, roomName3);
        chatRoomRepository.create(memberId3, roomName4);
        // when
        List<ChatRoom> all = chatRoomRepository.findAll();
        // then
        Assertions.assertTrue(() -> all.size() >= 2);
    }
    @Test
    void ChatRoom_삭제() {
        // given
        String roomName5 = "room5";
        Long memberId4 = 4L;
        ChatRoom chatRoom = chatRoomRepository.create(memberId4, roomName5);
        // when
        chatRoomRepository.delete(chatRoom);
        // then
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        Assertions.assertEquals(byId, Optional.empty());
    }
}
