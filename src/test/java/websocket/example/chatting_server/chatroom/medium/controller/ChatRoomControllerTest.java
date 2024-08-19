package websocket.example.chatting_server.chatroom.medium.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import websocket.example.chatting_server.chatRoom.controller.dto.ChatRoomEnterReqDto;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatRoomControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberChatRoomRepository memberChatRoomRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 채팅방_입장_성공() throws Exception {
        // given
        Long memberId1 = 1L;
        ChatRoom chatRoom1 = chatRoomService.create(memberId1, "room1");
        Long memberId2 = 2L;
        ChatRoomEnterReqDto reqDto = new ChatRoomEnterReqDto(memberId2, chatRoom1.getRoomId());
        // when
        MvcResult mvcResult = mockMvc.perform(post("/chatroom/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andReturn();
        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(chatRoom1.getRoomId());
        Assertions.assertEquals(byRoomId.size(), 2);
    }

    @Test
    void 채팅방_퇴장_성공() throws Exception {
        // given
        Long memberId3 = 3L;
        ChatRoom chatRoom3 = chatRoomService.create(memberId3, "room3");
        Long memberId4 = 4L;
        chatRoomService.enter(memberId4, chatRoom3.getRoomId());
        ChatRoomEnterReqDto reqDto = new ChatRoomEnterReqDto(memberId4, chatRoom3.getRoomId());
        Assertions.assertEquals(memberChatRoomRepository.findByRoomId(chatRoom3.getRoomId()).size(), 2);
        // when
        MvcResult mvcResult = mockMvc.perform(post("/chatroom/exit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andReturn();
        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(chatRoom3.getRoomId());
        Assertions.assertEquals(byRoomId.size(), 1);
    }
}