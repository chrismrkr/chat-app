package websocket.example.chatting_server.chatroom.medium.repository;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.event.ChatRoomExitEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ChatRoomEventHandlerTest {

    @Autowired
    ChatRoomEventHandler chatRoomEventHandler;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;

    @Test
    void ChatRoom에_참여자가_있는지_확인_후_없으면_채팅방_제거() throws InterruptedException {
        // given
        String roomName = "roomA";
        ChatRoom chatRoom = chatRoomRepository.create(roomName);

        // when
        chatRoomEventHandler.publishEmptyCheck(new ChatRoomExitEvent(chatRoom.getRoomId()));
        // then
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
                    Assertions.assertEquals(byId, Optional.empty());
                });
    }
}
