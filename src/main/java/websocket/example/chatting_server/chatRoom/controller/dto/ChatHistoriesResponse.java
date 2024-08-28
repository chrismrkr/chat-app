package websocket.example.chatting_server.chatRoom.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoriesResponse {
    private Long roomId;
    private List<ChatHistory> chatHistories;
}
