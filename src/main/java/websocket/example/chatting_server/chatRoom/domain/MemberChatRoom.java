package websocket.example.chatting_server.chatRoom.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberChatRoom {
    private Long memberId;
    private ChatRoom chatRoom;

    @Builder
    public MemberChatRoom(Long memberId, ChatRoom chatRoom) {
        this.memberId = memberId;
        this.chatRoom = chatRoom;
    }
    public MemberChatRoomEntity toEntity() {
        return MemberChatRoomEntity.builder()
                .chatRoomEntity(this.chatRoom.toEntity())
                .memberId(this.memberId)
                .build();
    }
    public static MemberChatRoom from(MemberChatRoomEntity entity) {
        return MemberChatRoom.builder()
                .chatRoom(ChatRoom.from(entity.getChatRoomEntity()))
                .memberId(entity.getMemberId())
                .build();
    }

}
