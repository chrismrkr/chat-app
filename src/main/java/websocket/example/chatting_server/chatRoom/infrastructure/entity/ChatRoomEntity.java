package websocket.example.chatting_server.chatRoom.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomEntity {
    @Id
    @GeneratedValue
    @Column(name = "room_id")
    private Long roomId;
    private String roomName;
    private Long memberId;

    @Builder
    public ChatRoomEntity(Long roomId, String roomName, Long memberId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.memberId = memberId;
    }

}
