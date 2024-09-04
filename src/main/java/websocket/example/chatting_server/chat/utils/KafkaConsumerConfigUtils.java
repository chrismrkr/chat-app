package websocket.example.chatting_server.chat.utils;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerConfigUtils {
    private String topicName;
    private String groupId;
}
