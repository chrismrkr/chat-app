package websocket.example.chatting_server.chatRoom.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatRoomCreateReqDto {
    @NotBlank
    private Long memberId;
    @NotBlank
    private String roomName;
}
