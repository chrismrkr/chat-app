package websocket.example.chatting_server.chat.infrastructure.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "chat_history")
@Getter
public class ChatHistoryEntity {
    @Id
    private Long seq;
    private final Long roomId;
    private final String senderName;
    @Field(type = FieldType.Text)
    private final String message;

    @Builder
    public ChatHistoryEntity(Long seq, Long roomId, String senderName, String message) {
        this.seq = seq;
        this.roomId = roomId;
        this.senderName = senderName;
        this.message = message;
    }
}
