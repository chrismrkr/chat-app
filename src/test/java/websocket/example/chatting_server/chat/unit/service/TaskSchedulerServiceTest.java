package websocket.example.chatting_server.chat.unit.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import websocket.example.chatting_server.chat.service.impl.TaskSchedulerServiceImpl;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskSchedulerServiceTest {
    TaskSchedulerServiceImpl taskSchedulerService;

    @BeforeEach
    void init() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("thread-pool-scheduler-");
        scheduler.initialize();
        this.taskSchedulerService = new TaskSchedulerServiceImpl(scheduler);
    }

    @Test
    void 주기적으로_scheduler를_실행할_수_있음() {
        // given
        Duration delay = Duration.ofMillis(1000);
        AtomicInteger count = new AtomicInteger(0);

        // when
        taskSchedulerService.start(() -> {
            System.out.println(count.incrementAndGet());
        }, delay);

        // then
        Awaitility.await()
                .atMost(3500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Assertions.assertTrue(count.get() >= 3);
                });
    }

}
