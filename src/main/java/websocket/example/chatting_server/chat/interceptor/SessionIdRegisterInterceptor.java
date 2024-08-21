package websocket.example.chatting_server.chat.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class SessionIdRegisterInterceptor implements ChannelInterceptor {
    private final ObjectMapper objectMapper;
    private final ChatIdGenerateUtils chatIdGenerateUtils;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor == null) {
            throw new IllegalArgumentException("STOMP Header NOT FOUND");
        }
        if(accessor.getCommand() == null || !accessor.getCommand().equals(StompCommand.SEND)) {
            return message;
        }
        try {
            String payload = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            ChatDto chatDto = objectMapper.readValue(payload, ChatDto.class);
            chatDto.setSenderSessionId(accessor.getSessionId());
            chatDto.setSeq(chatIdGenerateUtils.nextId());
            Message<?> newMessage = MessageBuilder
                    .createMessage(objectMapper.writeValueAsBytes(chatDto), message.getHeaders());
            return newMessage;
        } catch (JsonProcessingException e) {
            return message;
        }
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
    }
}
