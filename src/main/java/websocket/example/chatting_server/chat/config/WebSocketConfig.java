package websocket.example.chatting_server.chat.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;
    @Value("${spring.rabbitmq.relay-port}")
    private int rabbitmqPort;
    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // Kafka 사용
//        config.setApplicationDestinationPrefixes("/app");
//        config.enableSimpleBroker("/chatroom");
        // external broker
        config.setApplicationDestinationPrefixes("/app");
        config.enableStompBrokerRelay("/exchange")
                .setRelayHost(rabbitmqHost)
                .setRelayPort(rabbitmqPort)
                .setClientLogin(rabbitmqUsername)
                .setClientPasscode(rabbitmqPassword);

        // internal broker
        config.enableSimpleBroker("/internal");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
