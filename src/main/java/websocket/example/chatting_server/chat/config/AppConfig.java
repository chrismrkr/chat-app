package websocket.example.chatting_server.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final Environment environment;
    @Bean
    public ChatIdGenerateUtils chatIdGenerateUtils() {
        return new ChatIdGenerateUtils(Long.parseLong(environment.getProperty("server.worker.id")),
                                        Long.parseLong(environment.getProperty("server.datacenter.id")));
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
