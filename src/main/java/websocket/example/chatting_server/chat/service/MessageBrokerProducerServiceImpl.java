package websocket.example.chatting_server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.controller.port.MessageBrokerProducerService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBrokerProducerServiceImpl implements MessageBrokerProducerService {
    private final KafkaTemplate<String, ChatDto> kafkaTemplate;
    @Override
    public void sendMessage(String topic, ChatDto chatDto) {
        kafkaTemplate.send(topic, chatDto);
    }
}
