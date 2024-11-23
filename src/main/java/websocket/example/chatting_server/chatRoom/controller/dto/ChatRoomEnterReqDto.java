package websocket.example.chatting_server.chatRoom.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomEnterReqDto {
    @NotBlank
    private Long memberId;
    @NotBlank
    private Long roomId;
}
