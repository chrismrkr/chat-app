package websocket.example.chatting_server.chatroom.unit.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

public class ChatRoomDomainTest {
    @Test
    void ChatRoom을_ChatRoomEntity로_변환() {
        // given
        ChatRoom room1 = ChatRoom.builder()
                .memberId(1L)
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
    void ChatRoomEntity를_ChatRoom으로_변환() {
        // given
        ChatRoomEntity room2 = ChatRoomEntity.builder()
                .memberId(2L)
                .roomId(2L)
                .roomName("room2")
                .build();
        // when
        ChatRoom from = ChatRoom.from(room2);
        // then
        Assertions.assertEquals(room2.getRoomId(), from.getRoomId());
        Assertions.assertEquals(room2.getRoomName(), from.getRoomName());
    }
}
