package websocket.example.chatting_server.chatRoom.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {
    private Long roomId;
    private String roomName;
    private Long memberId;

    @Builder
    public ChatRoom(Long roomId, String roomName, Long memberId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.memberId = memberId;
    }

    public ChatRoomEntity toEntity() {
        return ChatRoomEntity.builder()
                .roomId(this.roomId)
                .roomName(this.roomName)
                .memberId(this.memberId)
                .build();
    }
    public static ChatRoom from(ChatRoomEntity chatRoomEntity) {
        return ChatRoom.builder()
                .roomId(chatRoomEntity.getRoomId())
                .roomName(chatRoomEntity.getRoomName())
                .memberId(chatRoomEntity.getMemberId())
                .build();
    }

}
