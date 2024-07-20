package websocket.example.chatting_server.chat.medium.controller;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import websocket.example.chatting_server.chat.controller.HealthCheckController;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.controller.dto.HealthCheckResponse;

import java.lang.reflect.Type;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckControllerTest {
    @LocalServerPort
    int port;
    @Autowired
    HealthCheckController healthCheckController;
    WebSocketStompClient stompClient;
    BlockingQueue<HealthCheckResponse> blockingQueue;

    @BeforeEach
    void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.blockingQueue = new LinkedBlockingDeque<>();
    }
    @Test
    void ExternalBroker가_정상이면_헬스체크를_주기적으로_받는다() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        String healthCheckDestination = "/internal/healthcheck";
        StompSession session = stompClient
                .connect("ws://localhost:" + port + "/ws",
                        new StompSessionHandlerAdapter() {
                        }).get(10, TimeUnit.SECONDS);
        session.subscribe(healthCheckDestination, new StompFrameHandler() {
          @Override
            public Type getPayloadType(StompHeaders headers) {
                return HealthCheckResponse.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((HealthCheckResponse) payload);
            }
        });
        // when
        // then
        HealthCheckResponse poll = blockingQueue.poll(25000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals("SUCCESS", poll.getStatus());
    }
}
