package websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey;

import lombok.*;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
@AllArgsConstructor
public class MemberChatRoomId implements Serializable {
    private Long memberId;
    private Long chatRoomEntity;
}