package websocket.example.chatting_server.chat.service;

import java.time.Duration;

public interface TaskSchedulerService {
    void start(Runnable runnable, Duration duration);
}
