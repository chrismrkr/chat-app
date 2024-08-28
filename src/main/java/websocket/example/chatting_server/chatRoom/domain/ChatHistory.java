package websocket.example.chatting_server.chatRoom.domain;

import lombok.Builder;
import lombok.Getter;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatHistoryEntity;

import java.time.LocalDateTime;

@Getter
public class ChatHistory {
    private Long seq;
    private final Long roomId;
    private final String senderName;
    private final String message;
    private final LocalDateTime sendTime;

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
