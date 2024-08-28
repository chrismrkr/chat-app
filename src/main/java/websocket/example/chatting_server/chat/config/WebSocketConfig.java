package websocket.example.chatting_server.chat.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.infrastructure.LockRepository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;
import websocket.example.chatting_server.chat.interceptor.DuplicatedMessageCheckInterceptor;
import websocket.example.chatting_server.chat.interceptor.SessionIdRegisterInterceptor;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ObjectMapper objectMapper;
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    private final LockRepository lockRepository;
    private final ChatIdGenerateUtils chatIdGenerateUtils;
    private final ChatRoomService chatRoomService;
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
    @Bean
    public SessionIdRegisterInterceptor sessionIdRegisterInterceptor() {
        return new SessionIdRegisterInterceptor(objectMapper, chatIdGenerateUtils);
    }
    @Bean
    public DuplicatedMessageCheckInterceptor duplicatedMessageCheckInterceptor() {
        return new DuplicatedMessageCheckInterceptor(objectMapper, outboundChannelHistoryRepository, lockRepository, chatRoomService);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionIdRegisterInterceptor());
    }
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(duplicatedMessageCheckInterceptor());
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
