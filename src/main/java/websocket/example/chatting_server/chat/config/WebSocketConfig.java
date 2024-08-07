package websocket.example.chatting_server.chat.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import websocket.example.chatting_server.chat.infrastructure.LockRepository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;
import websocket.example.chatting_server.chat.interceptor.DuplicatedMessageCheckInterceptor;
import websocket.example.chatting_server.chat.interceptor.SessionIdRegisterInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ObjectMapper objectMapper;
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    private final LockRepository lockRepository;
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
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SessionIdRegisterInterceptor(objectMapper));
    }
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new DuplicatedMessageCheckInterceptor(objectMapper, outboundChannelHistoryRepository, lockRepository));
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
