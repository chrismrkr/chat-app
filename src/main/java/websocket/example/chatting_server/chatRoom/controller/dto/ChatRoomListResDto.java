package websocket.example.chatting_server.chatRoom.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomListResDto {
    private int count;
    private List<ChatRoom> chatRoomList;
}
