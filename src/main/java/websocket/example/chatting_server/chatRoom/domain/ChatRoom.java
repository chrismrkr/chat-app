package websocket.example.chatting_server.chatRoom.domain;

import lombok.*;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatRoom {
    @EqualsAndHashCode.Include
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

    public ChatHistory createChatHistory(ChatDto chatDto) {
        ChatHistory newHistory = ChatHistory.builder()
                .seq(chatDto.getSeq())
                .roomId(chatDto.getRoomId())
                .senderName(chatDto.getSenderName())
                .message(chatDto.getMessage())
                .sendTime(LocalDateTime.now())
                .build();
        return newHistory;
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
        return ChatRoom.builder()
                .roomId(chatRoomEntity.getRoomId())
                .roomName(chatRoomEntity.getRoomName())
                .build();
    }

    public static ChatRoom fromWithParticipants(ChatRoomEntity chatRoomEntity) {
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
