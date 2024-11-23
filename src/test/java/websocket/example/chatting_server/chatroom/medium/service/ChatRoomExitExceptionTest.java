package websocket.example.chatting_server.chatroom.medium.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomCacheRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.service.impl.ChatRoomServiceImpl;
import websocket.example.chatting_server.chatRoom.service.inner.ChatRoomInnerService;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@SpringBootTest
public class ChatRoomExitExceptionTest {
    @SpyBean
    ChatRoomInnerService chatRoomInnerService;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;



    @Test
    void 인원이_없는_채팅방을_삭제하다가_실패하면_롤백() throws SQLIntegrityConstraintViolationException {
        // given
        doThrow(new RuntimeException("THROW MOCK ERROR")).when(chatRoomInnerService).checkEmpty(any());
        String roomName = "room";
        Long memberId = 1L;
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
        chatRoomService.exit(memberId, chatRoom.getRoomId());

        // then
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(chatRoom.getRoomId());
        Assertions.assertFalse(byRoomId.isEmpty());
        Optional<ChatRoom> byId = chatRoomRepository.findById(chatRoom.getRoomId());
        Assertions.assertFalse(byId.isEmpty());
    }

}
