package websocket.example.chatting_server.chatRoom.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_chat_room")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@IdClass(MemberChatRoomId.class)
@EntityListeners(AuditingEntityListener.class)
public class MemberChatRoomEntity implements Serializable {
    @Id
    @Column(name = "member_id")
    private Long memberId;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoomEntity;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime enterDateTime;

    @Builder
    public MemberChatRoomEntity(Long memberId, ChatRoomEntity chatRoomEntity, LocalDateTime enterDateTime) {
        this.memberId = memberId;
        this.chatRoomEntity = chatRoomEntity;
        this.enterDateTime = enterDateTime;
    }
}
