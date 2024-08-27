package websocket.example.chatting_server.chatRoom.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {
    private Long roomId;
    private String roomName;
    private Set<MemberChatRoom> participants = new HashSet<>();

    @Builder
    public ChatRoom(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public MemberChatRoom participate(Long memberId) {
        MemberChatRoom participant = MemberChatRoom.builder()
                .memberId(memberId)
                .chatRoom(this)
                .build();
        this.participants.add(participant);
        return participant;
    }

    public boolean exit(MemberChatRoom memberChatRoom) {
        return this.participants.remove(memberChatRoom);
    }

    public Optional<MemberChatRoom> findParticipants(Long memberId) {
        return this.participants.stream()
                .filter(memberChatRoom -> memberChatRoom.getMemberId().equals(memberId))
                .findAny();
    }

    public ChatRoomEntity toEntity() {
        return ChatRoomEntity.builder()
                .roomId(this.roomId)
                .roomName(this.roomName)
                .build();
    }
    public static ChatRoom from(ChatRoomEntity chatRoomEntity) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(chatRoomEntity.getRoomId())
                .roomName(chatRoomEntity.getRoomName())
                .build();
        chatRoomEntity.getMemberChatRoomEntities().forEach(entity -> {
            chatRoom.getParticipants()
                    .add(MemberChatRoom.builder()
                            .memberId(entity.getMemberId())
                            .chatRoom(chatRoom)
                            .enterDateTime(entity.getEnterDateTime())
                            .build());
        });
        return chatRoom;
    }
}
