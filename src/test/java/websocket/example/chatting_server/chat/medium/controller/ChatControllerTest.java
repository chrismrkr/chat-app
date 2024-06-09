package websocket.example.chatting_server.chat.medium.controller;

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
import websocket.example.chatting_server.chat.config.WebSocketConfig;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {
    @LocalServerPort
    int port;
    @Autowired
    WebSocketConfig webSocketConfig;
    WebSocketStompClient stompClient;
    BlockingQueue<ChatDto> blockingQueue;

    @BeforeEach
    void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.blockingQueue = new LinkedBlockingDeque<>();
    }

    @Test
    public void 싱글스레드_웹소켓_통신() throws Exception {
        // given
        StompSession session = stompClient
                .connect("ws://localhost:" + port + "/ws",
                        new StompSessionHandlerAdapter() {
                        }).get(10, TimeUnit.SECONDS);
        session.subscribe("/chatroom/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDto.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((ChatDto) payload);
            }
        });

        // when
        String roomId = "1";
        session.send("/app/message/" + roomId, new ChatDto(null, "USR1", "HELLO"));
        // then
        ChatDto poll = blockingQueue.poll(10, TimeUnit.SECONDS);
        Assertions.assertEquals(poll.getRoomId(), 1L);
        Assertions.assertEquals(poll.getMessage(), "HELLO");
        Assertions.assertEquals(poll.getSenderName(), "USR1");
    }

    @Test
    public void 멀티스레드_웹소켓_통신() throws Exception {
      // given
        int threadCount = 10;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        List<StompSession> sessionList = new ArrayList<>();
        for(int i=0; i<threadCount; i++) {
            StompSession session = stompClient
                    .connect("ws://localhost:" + port + "/ws",
                            new StompSessionHandlerAdapter() {
                            }).get(10, TimeUnit.SECONDS);
            session.subscribe("/chatroom/1", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return ChatDto.class;
                }
                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    blockingQueue.offer((ChatDto) payload);
                }
            });
            sessionList.add(session);
        }
        Thread.sleep(1000);
        // when
        String roomId = "1";
        sessionList.get(3).send("/app/message/" + roomId, new ChatDto(null, "USR1", "HELLO"));
        // then
        ChatDto poll = blockingQueue.poll(10, TimeUnit.SECONDS);
        Assertions.assertEquals(poll.getRoomId(), 1L);
        Assertions.assertEquals(poll.getMessage(), "HELLO");
        Assertions.assertEquals(poll.getSenderName(), "USR1");
    }
}
