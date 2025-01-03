package websocket.example.chatting_server.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.controller.dto.*;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@CrossOrigin(origins = "*")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomCreateResDto> handleCreate(@RequestBody ChatRoomCreateReqDto dto) {
        ChatRoom chatRoom = chatRoomService.create(dto.getMemberId(), dto.getRoomName());
        return new ResponseEntity<>(new ChatRoomCreateResDto(chatRoom.getRoomId(), chatRoom.getRoomName()), HttpStatus.OK);
    }

    @PostMapping("/enter")
    public ResponseEntity<ChatRoomEnterResDto> handleEnter(@RequestBody ChatRoomEnterReqDto dto) {
        MemberChatRoom enter = chatRoomService.enter(dto.getMemberId(), dto.getRoomId());
        ChatRoomEnterResDto res = ChatRoomEnterResDto.builder()
                .status("ENTER")
                .enterAt(enter.getEnterDateTime())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/exit")
    public ResponseEntity<ChatRoomEnterResDto> handleExit(@RequestBody ChatRoomEnterReqDto dto) throws SQLIntegrityConstraintViolationException {
        chatRoomService.exit(dto.getMemberId(), dto.getRoomId());
        ChatRoomEnterResDto res = ChatRoomEnterResDto.builder()
                .status("EXIT")
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ChatRoomListResDto> handleGettingAll() {
        List<ChatRoom> all = chatRoomService.findAll();
        return new ResponseEntity<>(new ChatRoomListResDto(all.size(), all), HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ChatRoomListResDto> handleGettingMyChatroom(@PathVariable String memberId) {
        List<ChatRoom> byMemberId = chatRoomService.findByMemberId(Long.parseLong(memberId));
        return new ResponseEntity<>(new ChatRoomListResDto(byMemberId.size(), byMemberId), HttpStatus.OK);
    }

    @GetMapping("/history/{roomId}/{memberId}")
    public ResponseEntity<ChatHistoriesResponse> getChatroomHistories(@PathVariable Long memberId, @PathVariable Long roomId,  @RequestParam(required = false) String seq, @RequestParam(required = false) String size) {
        List<ChatHistory> chatHistories = null;
        if(seq != null && size != null) {
            chatHistories = chatRoomService.readChatHistory(roomId, memberId, Long.parseLong(seq), Integer.parseInt(size));
        } else {
            chatHistories = chatRoomService.readChatHistoryCache(roomId, memberId);
        }
        return new ResponseEntity<>(new ChatHistoriesResponse(roomId, chatHistories), HttpStatus.OK);
    }
}
