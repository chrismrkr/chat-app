package websocket.example.chatting_server.chat.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.utils.KafkaConsumerConfigUtils;


import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment environment;

    @Bean
    public KafkaTemplate<Long, Long> kafkaLongLongTypeTemplate() {
        return new KafkaTemplate<>(longLongTypeProducerFactory());
    }
    @Bean
    public ProducerFactory<Long, Long> longLongTypeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB 배치 크기
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig() {
        KafkaConsumerConfigUtils kafkaConsumerConfigUtils = new KafkaConsumerConfigUtils();
        kafkaConsumerConfigUtils.setTopicName(environment.getProperty("spring.kafka.topic.chatroom-empty-check"));
        kafkaConsumerConfigUtils.setGroupId(environment.getProperty("spring.kafka.consumer.group-id"));
        return kafkaConsumerConfigUtils;
    }

}
