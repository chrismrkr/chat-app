package websocket.example.chatting_server.chat.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import websocket.example.chatting_server.chat.controller.dto.HealthCheckResponse;
import websocket.example.chatting_server.chat.service.TaskSchedulerService;

import java.security.Principal;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthCheckController {
    private final TaskSchedulerService taskSchedulerService;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final Environment env;

    @PostConstruct
    public void sendExternalBrokerHealthCheck() {
        String exchange = env.getProperty("spring.rabbitmq.healthcheck.exchange-name");
        Long delayMillis = Long.parseLong(
                env.getProperty("spring.rabbitmq.healthcheck.delay-millis")
        );
        taskSchedulerService.start(() -> {
            rabbitTemplate.convertAndSend(exchange, "", new HealthCheckResponse("SUCCESS", "ping"));
        }, Duration.ofMillis(delayMillis));
    }

    @RabbitListener(queues = "#{healthCheckQueue.name}")
    public void receiveHealthCheckMessage(HealthCheckResponse healthCheckResponse) {
        messagingTemplate.convertAndSendToUser("broadcast", "/healthcheck", healthCheckResponse);
    }
}
