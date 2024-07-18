package websocket.example.chatting_server.chat.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckResponse {
    private String status;
    private String message;
}
