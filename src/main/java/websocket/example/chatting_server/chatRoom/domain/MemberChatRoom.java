package websocket.example.chatting_server.chatRoom.domain;

import lombok.*;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MemberChatRoom {
    @EqualsAndHashCode.Include
    private Long memberId;
    @EqualsAndHashCode.Include
    private ChatRoom chatRoom;
    private LocalDateTime enterDateTime;

    @Builder
    public MemberChatRoom(Long memberId, ChatRoom chatRoom, LocalDateTime enterDateTime) {
        this.memberId = memberId;
        this.chatRoom = chatRoom;
        this.enterDateTime = enterDateTime;
    }
    public MemberChatRoomEntity toEntity() {
        return MemberChatRoomEntity.builder()
                .chatRoomEntity(this.chatRoom.toEntity())
                .memberId(this.memberId)
                .enterDateTime(this.enterDateTime)
                .build();
    }
    public static MemberChatRoom from(MemberChatRoomEntity entity) {
        return MemberChatRoom.builder()
                .chatRoom(ChatRoom.from(entity.getChatRoomEntity()))
                .memberId(entity.getMemberId())
                .enterDateTime(entity.getEnterDateTime())
                .build();
    }

}
