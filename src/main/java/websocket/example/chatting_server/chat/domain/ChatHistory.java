package websocket.example.chatting_server.chat.domain;

import lombok.Builder;
import lombok.Getter;
import websocket.example.chatting_server.chat.infrastructure.entity.ChatHistoryEntity;

@Getter
public class ChatHistory {
    private Long seq;
    private final Long roomId;
    private final String senderName;
    private final String message;

    @Builder
    public ChatHistory(Long seq, Long roomId, String senderName, String message) {
        this.seq = seq;
        this.roomId = roomId;
        this.senderName = senderName;
        this.message = message;
    }

    public static ChatHistory from(ChatHistoryEntity entity) {
        return ChatHistory.builder()
                .seq(entity.getSeq())
                .roomId(entity.getRoomId())
                .senderName(entity.getSenderName())
                .message(entity.getMessage())
                .build();
    }
    public ChatHistoryEntity toEntity() {
        return ChatHistoryEntity.builder()
                .seq(this.getSeq())
                .roomId(this.getRoomId())
                .senderName(this.getSenderName())
                .message(this.getMessage())
                .build();
    }
}
