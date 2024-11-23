package websocket.example.chatting_server.chatRoom.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatHistoryEntity;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ChatHistory {
    @EqualsAndHashCode.Include
    private Long seq;
    @EqualsAndHashCode.Include
    private Long roomId;
    private String senderName;
    private String message;
    private LocalDateTime sendTime;

    @Builder
    public ChatHistory(Long seq, Long roomId, String senderName, String message, LocalDateTime sendTime) {
        this.seq = seq;
        this.roomId = roomId;
        this.senderName = senderName;
        this.message = message;
        this.sendTime = sendTime;
    }
    public static ChatHistory from(ChatHistoryEntity entity) {
        return ChatHistory.builder()
                .seq(entity.getSeq())
                .roomId(entity.getRoomId())
                .senderName(entity.getSenderName())
                .message(entity.getMessage())
                .sendTime(entity.getSendTime())
                .build();
    }
    public static ChatHistory from(ChatDto chatDto) {
        return ChatHistory.builder()
                .seq(chatDto.getSeq())
                .roomId(chatDto.getRoomId())
                .senderName(chatDto.getSenderName())
                .message(chatDto.getMessage())
                .sendTime(LocalDateTime.now())
                .build();
    }
    public ChatHistoryEntity toEntity() {
        return ChatHistoryEntity.builder()
                .seq(this.getSeq())
                .roomId(this.getRoomId())
                .senderName(this.getSenderName())
                .message(this.getMessage())
                .sendTime(this.getSendTime())
                .build();
    }
}
