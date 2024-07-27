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
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chat.infrastructure.LockRepository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;

import java.awt.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class DuplicatedMessageCheckInterceptor implements ChannelInterceptor {
    private final ObjectMapper objectMapper;
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    private final LockRepository lockRepository;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(!isMessageCommand(accessor)) {
            return message;
        }
        try {
            ChatDto chatDto = fromPayload(message);
            String senderSessionId = chatDto.getSenderSessionId();
            String receiverSessionId = accessor.getSessionId();

            if(!lockRepository.holdLock(
                    senderSessionId + "-" + receiverSessionId + "#" + chatDto.getSeq().toString(),
                    chatDto.getSeq().toString())) {
                // 이미 전송 중인 메세지 Sequence
                throw new IllegalArgumentException(
                        "[MESSAGE SEND ERROR] MESSAGE SEQUENCE "
                        + chatDto.getSeq().toString()
                                +" ALREADY SENT");
            }
            if(!isReliableSequence(receiverSessionId, senderSessionId, chatDto.getSeq())) {
                // 이미 전송 완료된 메세지 Sequence
                throw new IllegalArgumentException("[MESSAGE SEND ERROR] MESSAGE SEQUENCE NOT VALID");
            }
        }  catch (JsonProcessingException e) {
            return message;
        }
        return message;
    }
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        ChatDto chatDto = null;
        try {
            chatDto = fromPayload(message);
        } catch (Exception e) {
            return;
        }

        String senderSessionId = chatDto.getSenderSessionId();
        String receiverSessionId = accessor.getSessionId();
        if(isMessageCommand(accessor)) {
            int nextSeq = chatDto.getSeq();
            outboundChannelHistoryRepository.updateSequence(receiverSessionId, senderSessionId, nextSeq);
        }
        lockRepository.releaseLock(senderSessionId + "-" + receiverSessionId + "#" + chatDto.getSeq().toString());
    }

    private boolean isReliableSequence(String receiverSessionId, String senderSessionId, int newSeq) {
        if(!outboundChannelHistoryRepository.isSenderSessionExists(receiverSessionId, senderSessionId)) {
            outboundChannelHistoryRepository.updateSequence(receiverSessionId, senderSessionId, -1);
            return true;
        }
        if(outboundChannelHistoryRepository.getSequence(receiverSessionId, senderSessionId) < newSeq) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isMessageCommand(StompHeaderAccessor accessor) {
        return accessor.getCommand() == null || !accessor.getCommand().equals(StompCommand.MESSAGE) ? false : true;
    }

    private ChatDto fromPayload(Message<?> message) throws JsonProcessingException {
        String payload = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
        ChatDto chatDto = objectMapper.readValue(payload, ChatDto.class);
        return chatDto;
    }
}
