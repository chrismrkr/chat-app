package websocket.example.chatting_server.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    private final RabbitTemplate rabbitMQTemplate;
    private final Environment env;

    @MessageMapping("/message/{roomId}") // pub : /app/message/{roomId}
    public void sendToMessageBroker(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
        ChatDto dto = new ChatDto(Long.parseLong(roomId), chatDto.getSenderName(), chatDto.getMessage());
        String exchange = env.getProperty("spring.rabbitmq.chat.exchange-name");
        String routingKey = env.getProperty("spring.rabbitmq.chat.routing-key") + roomId;
        rabbitMQTemplate.convertAndSend(exchange, routingKey, dto);
    }

//    Kafka 사용 시 사용
//    @Value("${spring.kafka.consumer.topic-name}")
//    private String topic;
//    private final MessageBrokerProduceService messageBrokerProduceService;
//    @MessageMapping("/message/{roomId}") // pub : /app/message/{roomId}
//    public void sendToMessageBroker(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
//        ChatDto dto = new ChatDto(Long.parseLong(roomId), chatDto.getSenderName(), chatDto.getMessage());
//        messageBrokerProduceService.broadcastToCluster(topic, dto);
//    }
//
//    @SendTo("/chatroom/{roomId}")  // sub
//    public ChatDto subscribe(@RequestBody ChatDto chatDto, @DestinationVariable String roomId) throws Exception {
//        return new ChatDto(Long.parseLong(roomId), chatDto.getSenderName(), chatDto.getMessage());
//    }
}
