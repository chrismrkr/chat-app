package websocket.example.chatting_server.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableRabbit
@Slf4j
public class RabbitMQMessageBrokerConfig {
    @Value("${spring.rabbitmq.chat.queue-name}")
    private String CHAT_QUEUE_NAME;
    @Value("${spring.rabbitmq.chat.exchange-name}")
    private String CHAT_EXCHANGE_NAME;
    @Value("${spring.rabbitmq.chat.binding-key}")
    private String BINDING_KEY;
    @Value("${spring.rabbitmq.healthcheck.queue-name}")
    private String HEALTH_CHECK_QUEUE_NAME;
    @Value("${spring.rabbitmq.healthcheck.exchange-name}")
    private String HEALTH_CHECK_EXCHANGE_NAME;
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;
    @Value("${spring.rabbitmq.amqp-port}")
    private int rabbitmqPort;
    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Bean
    @Qualifier("chatQueue")
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }
    @Bean
    @Qualifier("chatExchange")
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE_NAME);
    }
    @Bean
    @Qualifier("chatBinding")
    public Binding chatbinding(@Qualifier("chatQueue") Queue queue, @Qualifier("chatExchange") TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(BINDING_KEY);
    }

    @Bean
    @Qualifier("healthCheckQueue")
    public Queue healthCheckQueue() {
        return new Queue(HEALTH_CHECK_QUEUE_NAME, true);
    }
    @Bean
    @Qualifier("healthCheckExchange")
    public FanoutExchange healthCheckExchange() {
        return new FanoutExchange(HEALTH_CHECK_EXCHANGE_NAME);
    }
    @Bean
    @Qualifier("healthCheckBinding")
    public Binding healthCheckBinding(@Qualifier("healthCheckQueue") Queue queue, @Qualifier("healthCheckExchange") FanoutExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange);
    }

    // RabbitMQ와의 메시지 통신을 담당하는 클래스
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            if(ack) {

            } else {
                log.info("[MESSAGE PUBLISH CONFIRM FAIL] {}", cause);
            }
        }));
        return rabbitTemplate;
    }

    // RabbitMQ와의 연결을 관리하는 클래스
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        factory.setHost(rabbitmqHost);
        factory.setPort(rabbitmqPort);
        factory.setUsername(rabbitmqUsername);
        factory.setPassword(rabbitmqPassword);
        return factory;
    }

    // 메시지를 JSON형식으로 직렬화하고 역직렬화하는데 사용되는 변환기
    // RabbitMQ 메시지를 JSON형식으로 보내고 받을 수 있음
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
