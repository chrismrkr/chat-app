package websocket.example.chatting_server.chat.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import websocket.example.chatting_server.chat.service.ExternalBrokerHealthCheckService;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalBrokerHealthCheckServiceImpl implements ExternalBrokerHealthCheckService {
    private final TaskScheduler threadPoolTaskScheduler;
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    @Override
    public void start() {
//        threadPoolTaskScheduler.scheduleWithFixedDelay(() -> {
//            /* Impl external broker health check
//            ** 1. Do health check
//            ** 2. Send health check result To User
//            */
//            rabbitTemplate.convertAndSend();
//        }, Duration.ofMillis(20000));
    }
}
