package websocket.example.chatting_server.chat.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.chat.service.TaskSchedulerService;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerServiceImpl implements TaskSchedulerService {
    private final TaskScheduler threadPoolTaskScheduler;
    @Override
    public void start(Runnable runnable, Duration duration) {
        threadPoolTaskScheduler.scheduleWithFixedDelay(
                runnable, 
                duration);
    }
}
