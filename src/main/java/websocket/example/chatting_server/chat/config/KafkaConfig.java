package websocket.example.chatting_server.chat.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.listener.ContainerProperties;
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

    @Bean(name = "chatRoomEmptyCheckKafkaProducerTemplate")
    public KafkaTemplate<Long, Long> chatRoomEmptyCheckKafkaProducerTemplate() {
        return new KafkaTemplate<>(longLongTypeProducerFactory());
    }
    @Bean
    public ProducerFactory<Long, Long> longLongTypeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB 배치 크기
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "chatRoomEmptyCheckConsumerConfig")
    public KafkaConsumerConfigUtils chatRoomEmptyCheckConsumerConfig() {
        KafkaConsumerConfigUtils kafkaConsumerConfigUtils = new KafkaConsumerConfigUtils();
        kafkaConsumerConfigUtils.setTopicName(environment.getProperty("spring.kafka.topic.chatroom-empty-check"));
        kafkaConsumerConfigUtils.setGroupId(environment.getProperty("spring.kafka.consumer.group-id"));
        return kafkaConsumerConfigUtils;
    }

    @Bean
    public ConsumerFactory<Long, Long> chatroomEmptyCheckEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동 오프셋 커밋
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean(name = "chatRoomEmptyCheckKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Long, Long> chatRoomEmptyCheckKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatroomEmptyCheckEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
