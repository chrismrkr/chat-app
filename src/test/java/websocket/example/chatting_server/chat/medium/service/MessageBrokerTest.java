package websocket.example.chatting_server.chat.medium.service;

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
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.service.MessageBrokerProduceService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageBrokerTest {
    @LocalServerPort
    int port;
    WebSocketStompClient stompClient;
    BlockingQueue<ChatDto> blockingQueue;
    @Autowired
    MessageBrokerProduceService messageBrokerProduceService;
    @BeforeEach
    void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.blockingQueue = new LinkedBlockingDeque<>();
    }
    @Test
    void chat_topic_메세지를_produce하면_consume할_수_있다() throws Exception {
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
        String topic = "chat-topic";
        ChatDto chatDto = new ChatDto(1L, "USR1","Hello Everyone");
        messageBrokerProduceService.broadcastToCluster(topic, chatDto);
        // then
        ChatDto poll = blockingQueue.poll(10, TimeUnit.SECONDS);
        Assertions.assertNotNull(poll);
        Assertions.assertEquals(poll.getRoomId(), 1L);
        Assertions.assertEquals(poll.getMessage(), "Hello Everyone");
        Assertions.assertEquals(poll.getSenderName(), "USR1");
        ChatDto pollNull = blockingQueue.poll(10, TimeUnit.SECONDS);
        Assertions.assertNull(pollNull);

    }
    @Test
    void chat_topic_메세지를_produce하면_여러_consumer가_받을_수_있다() throws Exception{
        // given
        int threadCount = 10;
        List<StompSession> sessionList = new ArrayList<>();
        for(int i=0; i<threadCount; i++) {
            StompSession session = stompClient
                    .connect("ws://localhost:" + port + "/ws",
                            new StompSessionHandlerAdapter() {
                            }).get(10, TimeUnit.SECONDS);
            session.subscribe("/chatroom/2", new StompFrameHandler() {
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
        // when
        String topic = "chat-topic";
        ChatDto chatDto = new ChatDto(2L, "USR1","Hello Everyone");
        messageBrokerProduceService.broadcastToCluster(topic, chatDto);
        // then
        for(int i=0; i<threadCount; i++) {
            ChatDto poll = blockingQueue.poll(10, TimeUnit.SECONDS);
            Assertions.assertEquals(poll.getRoomId(), 2L);
            Assertions.assertEquals(poll.getSenderName(), "USR1");
            Assertions.assertEquals(poll.getMessage(), "Hello Everyone");
        }
        ChatDto pollNull = blockingQueue.poll(10, TimeUnit.SECONDS);
        Assertions.assertNull(pollNull);
    }

    @Test
    void roomId로_구분하여_chat_topic_메세지를_consume할_수_있다() throws Exception {

    }
}
