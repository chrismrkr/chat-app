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
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.infrastructure.LockRepository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class DuplicatedMessageCheckInterceptor implements ChannelInterceptor {
    private final ObjectMapper objectMapper;
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    private final LockRepository lockRepository;
    private final ChatRoomService chatRoomService;
    @Override
    @Transactional
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        ChatDto chatDto = null;
        if(!isMessageCommand(accessor)) {
            return message;
        }
        try {
            chatDto = fromPayload(message);
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
                Long exSeq = outboundChannelHistoryRepository.getSequence(receiverSessionId, senderSessionId);
                // TODO. throw 시 웹 소켓 세션이 종료되므로 세션이 끊기지 않는 쪽으로 변경 필요
                throw new IllegalArgumentException("[MESSAGE SEND ERROR] MESSAGE SEQUENCE INVALID :"
                        + "current: "+ chatDto.getSeq() + " , " + "ex : " + exSeq);
            }
        }  catch (JsonProcessingException e) {
            return message;
        }

        try {
            ChatHistory chatHistory = chatRoomService.writeChatHistory(chatDto.getRoomId(), chatDto);
        } catch (Exception e) {
            // TODO. 실패 시 Rollback 필요
            throw e;
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
            long nextSeq = chatDto.getSeq();
            outboundChannelHistoryRepository.updateSequence(receiverSessionId, senderSessionId, nextSeq);
        }
        lockRepository.releaseLock(
                senderSessionId + "-" + receiverSessionId + "#" + chatDto.getSeq().toString(),
                Long.toString(chatDto.getSeq())
                );
    }

    private boolean isReliableSequence(String receiverSessionId, String senderSessionId, long newSeq) {
        if(!outboundChannelHistoryRepository.isSenderSessionExists(receiverSessionId, senderSessionId)) {
            outboundChannelHistoryRepository.updateSequence(receiverSessionId, senderSessionId, -1L);
            return true;
        }
        long exSeq = outboundChannelHistoryRepository.getSequence(receiverSessionId, senderSessionId);
        if(newSeq > exSeq) {
            return true;
        } else return false;
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
