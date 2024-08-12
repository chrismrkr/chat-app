package websocket.example.chatting_server.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chatRoom.controller.dto.*;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@CrossOrigin(origins = "*")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomCreateResDto> create(@RequestBody ChatRoomCreateReqDto dto) {
        ChatRoom chatRoom = chatRoomService.create(dto.getMemberId(), dto.getRoomName());
        return new ResponseEntity<>(new ChatRoomCreateResDto(chatRoom.getRoomId(), chatRoom.getRoomName()), HttpStatus.OK);
    }

    @PostMapping("/enter")
    public ResponseEntity<ChatRoomEnterResDto> enter(@RequestBody ChatRoomEnterReqDto dto) {
        MemberChatRoom enter = chatRoomService.enter(dto.getMemberId(), dto.getRoomId());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/exit")
    public ResponseEntity<ChatRoomEnterResDto> exit(@RequestBody ChatRoomEnterReqDto dto) {
        chatRoomService.exit(dto.getMemberId(), dto.getRoomId());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<AllChatRoomResDto> findAll() {
        List<ChatRoom> all = chatRoomService.findAll();
        return new ResponseEntity<>(new AllChatRoomResDto(all.size(), all), HttpStatus.OK);
    }
}
