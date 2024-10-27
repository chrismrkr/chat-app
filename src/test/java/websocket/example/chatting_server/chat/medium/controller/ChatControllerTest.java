package websocket.example.chatting_server.chat.medium.controller;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import websocket.example.chatting_server.chat.config.RabbitMQConfig;
import websocket.example.chatting_server.chat.config.WebSocketConfig;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.infrastructure.impl.InMemoryOutboundChannelHistoryRepository;
import websocket.example.chatting_server.chat.infrastructure.impl.RedisLockRepositoryImpl;
import websocket.example.chatting_server.chat.interceptor.SessionIdRegisterInterceptor;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;

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
    RabbitMQConfig rabbitMQConfig;
    @Autowired
    InMemoryOutboundChannelHistoryRepository outboundChannelHistoryRepository;
    @Autowired
    RedisLockRepositoryImpl redisLockRepository;
    @Autowired
    ChatIdGenerateUtils chatIdGenerateUtils;
    @Autowired
    SessionIdRegisterInterceptor sessionIdRegisterInterceptor;
    @Autowired
    ChatHistoryRepository chatHistoryRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
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
    void 웹소켓_1개_연결이_메세지를_전달할_수_있다() throws Exception {
        // given
        String roomId = "1";
        ChatRoom chatRoom = chatRoomRepository.create("room" + roomId);
        connectedSession.subscribe("/exchange/chat.exchange/roomId." + chatRoom.getRoomId(), new StompFrameHandler() {
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
        connectedSession.send("/app/message/" + chatRoom.getRoomId(),
                ChatDto.builder()
                        .roomId(chatRoom.getRoomId())
                        .senderName("USR1")
                        .message("HELLO")
                        .build()
        );

        // then
        ChatDto chatDto = completableFuture.get(1000, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(chatRoom.getRoomId(), chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
        Assertions.assertEquals(false,
                redisLockRepository.isLocked(chatDto.getSenderSessionId()+"-"+chatDto.getSenderSessionId()+"#0"));
        Assertions.assertNotNull(chatHistoryRepository.findBySeq(chatDto.getSeq()));
        chatHistoryRepository.deleteBySeq(chatDto.getSeq());
    }

    @Test
    void 웹소켓_N개_연결이_메세지를_전달할_수_있다() throws Exception {
        // given
        int sessionCount = 100;
        List<StompSession> connectedSessionList = new ArrayList<>();
        List<CompletableFuture<Void>> sessionReadyList = new ArrayList<>();
        List<CompletableFuture<ChatDto>> completableFutureList = new ArrayList<>();
        List<ChatRoom> chatRoomList = new ArrayList<>();
        for(int i=0; i<sessionCount; i++) {
            String roomId = Integer.toString(i);
            ChatRoom chatRoom = chatRoomRepository.create("room" + roomId);
            chatRoomList.add(chatRoom);
        }
        for(int i=0; i<sessionCount; i++) {
            CompletableFuture<Void> sessionReady = new CompletableFuture<>();
            StompSession newStomp = stompClient
                    .connect("ws://localhost:" + port + "/ws",
                            new StompSessionHandlerAdapter() {
                                @Override
                                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                                    sessionReady.complete(null);
                                }

                            }).get(10, TimeUnit.SECONDS);
            sessionReady.get(5, TimeUnit.SECONDS);
            Awaitility.await()
                    .atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        Assertions.assertTrue(newStomp.isConnected());
                    });
            connectedSessionList.add(newStomp);
            sessionReadyList.add(sessionReady);
            completableFutureList.add(new CompletableFuture<>());
        }
        for(int i=0; i<sessionCount; i++) {
            int finalI = i;
            connectedSessionList.get(i).subscribe("/exchange/chat.exchange/roomId." + chatRoomList.get(i).getRoomId(), new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return ChatDto.class;
                }
                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    completableFutureList.get(finalI).complete((ChatDto) payload);
                }
            });
        }
        Thread.sleep(1000);

        // when
        for(int i=0; i<sessionCount; i++) {
            connectedSessionList.get(i).send("/app/message/" + chatRoomList.get(i).getRoomId(),
                    ChatDto.builder()
                            .roomId(chatRoomList.get(i).getRoomId())
                            .senderName("USR" + chatRoomList.get(i).getRoomId())
                            .message("HELLO" + chatRoomList.get(i).getRoomId())
                            .build()
            );
        }

        // then
        for(int i=0; i<sessionCount; i++) {
            ChatRoom chatRoom = chatRoomList.get(i);
            String roomId = Long.toString(chatRoom.getRoomId());
            ChatDto chatDto = completableFutureList.get(i).get(300, TimeUnit.SECONDS);
            Assertions.assertNotNull(chatDto);
            Assertions.assertEquals(Integer.parseInt(roomId), chatDto.getRoomId());
            Assertions.assertEquals("USR" + roomId, chatDto.getSenderName());
            Assertions.assertEquals("HELLO" + roomId, chatDto.getMessage());
            Assertions.assertNotNull(chatDto.getSenderSessionId());
            Assertions.assertEquals(false,
                    redisLockRepository.isLocked(chatDto.getSenderSessionId()+"-"+chatDto.getSenderSessionId()+"#0"));
            Assertions.assertNotNull(chatHistoryRepository.findBySeq(chatDto.getSeq()));
            chatHistoryRepository.deleteBySeq(chatDto.getSeq());
        }
    }

    @Test
    void 메세지를_전달하면_message_seq를_기록한다() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        String roomId = "1";
        ChatRoom chatRoom = chatRoomRepository.create("room" + roomId);
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
        connectedSession.send("/app/message/" + chatRoom.getRoomId(),
                ChatDto.builder()
                        .roomId(chatRoom.getRoomId())
                        .senderName("USR1")
                        .message("HELLO")
                        .build()
        );
        // then
        ChatDto chatDto = completableFuture.get(20, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(chatRoom.getRoomId(), chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
        Assertions.assertNotNull(chatDto.getSeq());
        Assertions.assertEquals(chatDto.getSeq(), outboundChannelHistoryRepository.getSequence(chatDto.getSenderSessionId(), chatDto.getSenderSessionId()));
        Assertions.assertNotNull(chatHistoryRepository.findBySeq(chatDto.getSeq()));
        chatHistoryRepository.deleteBySeq(chatDto.getSeq());
    }

    @Test
    void 동일한_seq로_메세지를_순서대로_전달하면_2번째_부터는_무시된다() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        ChatIdGenerateUtils mockChatIdGenerateUtils = Mockito.mock(ChatIdGenerateUtils.class);
        Mockito.when(mockChatIdGenerateUtils.nextId()).thenReturn(1825044961289043968L);
        ReflectionTestUtils.setField(sessionIdRegisterInterceptor, "chatIdGenerateUtils", mockChatIdGenerateUtils);
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
        String roomId = "1";// ex. 1825044961289043968
        ChatRoom chatRoom1 = chatRoomRepository.create("room" + roomId);
        connectedSession.send("/app/message/" + chatRoom1.getRoomId(),
                ChatDto.builder()
                        .roomId(chatRoom1.getRoomId())
                        .senderName("USR1")
                        .message("HELLO1")
                        .build()
        );
        // then1
        ChatDto chatDto = completableFuture.get(20, TimeUnit.SECONDS);
        Assertions.assertNotNull(chatDto);
        Assertions.assertEquals(chatRoom1.getRoomId(), chatDto.getRoomId());
        Assertions.assertEquals("USR1", chatDto.getSenderName());
        Assertions.assertEquals("HELLO1", chatDto.getMessage());
        Assertions.assertNotNull(chatDto.getSenderSessionId());
        Assertions.assertEquals(chatDto.getSeq(), outboundChannelHistoryRepository.getSequence(chatDto.getSenderSessionId(), chatDto.getSenderSessionId()));
        Assertions.assertNotNull(chatHistoryRepository.findBySeq(chatDto.getSeq()));
        chatHistoryRepository.deleteBySeq(chatDto.getSeq());

        // when2
        completableFuture = new CompletableFuture<>();
        connectedSession.send("/app/message/" + chatRoom1.getRoomId(),
                ChatDto.builder()
                        .roomId(chatRoom1.getRoomId())
                        .senderName("USR1")
                        .message("HELLO2")
                        .build()
        );
        // then2
        Assertions.assertThrows(TimeoutException.class,() -> completableFuture.get(10, TimeUnit.SECONDS));
    }
}
