package websocket.example.chatting_server.chatRoom.infrastructure.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "chat_history")
@Getter
public class ChatHistoryEntity {
    @Id
    private Long seq;
    private final Long roomId;
    private final String senderName;
    @Field(type = FieldType.Text)
    private final String message;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private final LocalDateTime sendTime;

    @Builder
    public ChatHistoryEntity(Long seq, Long roomId, String senderName, String message, LocalDateTime sendTime) {
        this.seq = seq;
        this.roomId = roomId;
        this.senderName = senderName;
        this.message = message;
        this.sendTime = sendTime;
    }
}
