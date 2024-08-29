package websocket.example.chatting_server.chatRoom.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;
    private String roomName;

    @OneToMany(mappedBy = "chatRoomEntity", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<MemberChatRoomEntity> memberChatRoomEntities = new HashSet<>();

    @Builder
    public ChatRoomEntity(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

}
