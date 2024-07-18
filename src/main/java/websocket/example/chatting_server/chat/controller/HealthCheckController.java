package websocket.example.chatting_server.chat.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import websocket.example.chatting_server.chat.service.TaskSchedulerService;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthCheckController {
    private final TaskSchedulerService taskSchedulerService;
    private final RabbitTemplate rabbitTemplate;
    private final Environment env;

    @PostConstruct
    public void scheduleExternalBrokerHealthCheck() {
        String queue = env.getProperty("spring.rabbitmq.healthcheck.queue-name");
        Long delayMillis = Long.parseLong(
                env.getProperty("spring.rabbitmq.healthcheck.delay-millis")
        );
        
        taskSchedulerService.start(() -> {
            rabbitTemplate.convertAndSend(queue, "");
        }, Duration.ofMillis(delayMillis));
    }
}
