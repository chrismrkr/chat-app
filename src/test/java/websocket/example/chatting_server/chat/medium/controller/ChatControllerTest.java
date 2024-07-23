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
import websocket.example.chatting_server.chat.config.RabbitMQMessageBrokerConfig;
import websocket.example.chatting_server.chat.config.WebSocketConfig;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.infrastructure.impl.InMemoryOutboundChannelHistoryRepository;

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
    @Autowired
    RabbitMQMessageBrokerConfig rabbitMQMessageBrokerConfig;
    @Autowired
    InMemoryOutboundChannelHistoryRepository outboundChannelHistoryRepository;
    WebSocketStompClient stompClient;

    CompletableFuture<ChatDto> completableFuture;
    CompletableFuture<Void> sessionConnectedReady;
    StompSession connectedSession;

    @BeforeEach
    void setup() throws ExecutionException, InterruptedException, TimeoutException {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.completableFuture = new CompletableFuture<>();
        this.sessionConnectedReady = new CompletableFuture<>();
        this.connectedSession = stompClient
                .connect("ws://localhost:" + port + "/ws",
                        new StompSessionHandlerAdapter() {
                            @Override
                            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                                sessionConnectedReady.complete(null);
                            }

                        }).get(10, TimeUnit.SECONDS);
        sessionConnectedReady.get(5, TimeUnit.SECONDS);
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertTrue(connectedSession.isConnected());
                });
    }

    @Test
    void 웹소켓을_통해_메세지를_전달할_수_있다() throws Exception {
        // given
        connectedSession.subscribe("/exchange/chat.exchange/roomId.1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDto.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((ChatDto) payload);
            }
        });
        Thread.sleep(1000);

        // when
        String roomId = "1";
        connectedSession.send("/app/message/" + roomId,
                ChatDto.builder()
                        .senderName("USR1")
                        .message("HELLO")
                        .seq(0)
                        .build()
        );

        // then
        ChatDto chatDto = completableFuture.get(20, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(1L, chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
    }

    @Test
    void 메세지를_전달하면_message_seq를_기록한다() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        connectedSession.subscribe("/exchange/chat.exchange/roomId.1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDto.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((ChatDto) payload);
            }
        });
        Thread.sleep(1000);

        // when
        String roomId = "1";
        connectedSession.send("/app/message/" + roomId,
                ChatDto.builder()
                        .seq(0)
                        .senderName("USR1")
                        .message("HELLO")
                        .build()
        );
        // then
        ChatDto chatDto = completableFuture.get(20, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(1L, chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
        Assertions.assertEquals(0, chatDto.getSeq());
        Assertions.assertEquals(0, outboundChannelHistoryRepository.getSequence(chatDto.getSenderSessionId(), chatDto.getSenderSessionId()));
    }

    @Test
    void 동일한_seq로_메세지를_2번_전달하면_2번째_메세지는_무시된다() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        connectedSession.subscribe("/exchange/chat.exchange/roomId.1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatDto.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((ChatDto) payload);
            }
        });
        Thread.sleep(1000);

        // when1
        String roomId = "1";
        connectedSession.send("/app/message/" + roomId,
                ChatDto.builder()
                        .seq(0)
                        .senderName("USR1")
                        .message("HELLO1")
                        .build()
        );
        // then1
        ChatDto chatDto = completableFuture.get(20, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(1L, chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO1", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
        Assertions.assertEquals(0, chatDto.getSeq());
        Assertions.assertEquals(0, outboundChannelHistoryRepository.getSequence(chatDto.getSenderSessionId(), chatDto.getSenderSessionId()));

        // when2
        completableFuture = new CompletableFuture<>();
        connectedSession.send("/app/message/" + roomId,
                ChatDto.builder()
                        .seq(0)
                        .senderName("USR1")
                        .message("HELLO2")
                        .build()
        );
        // then2
        Assertions.assertThrows(TimeoutException.class,() -> completableFuture.get(10, TimeUnit.SECONDS));
    }


}
