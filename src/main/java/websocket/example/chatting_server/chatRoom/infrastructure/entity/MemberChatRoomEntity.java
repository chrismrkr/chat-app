package websocket.example.chatting_server.chatRoom.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.io.Serializable;

@Entity(name = "member_chat_room")
@Table(name = "member_chat_room")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@IdClass(MemberChatRoomId.class)
public class MemberChatRoomEntity implements Serializable {
    @Id
    @Column(name = "member_id")
    private Long memberId;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoomEntity;

    @Builder
    public MemberChatRoomEntity(Long memberId, ChatRoomEntity chatRoomEntity) {
        this.memberId = memberId;
        this.chatRoomEntity = chatRoomEntity;
    }
}
