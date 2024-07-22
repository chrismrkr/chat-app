package websocket.example.chatting_server.chat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventServiceImpl implements WebsocketEventService {
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    @Override
    @EventListener
    public void handleOutboundChannelConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info(headerAccessor.getSessionId() + ": " + headerAccessor.getMessage());
        // history 생성
    }

    @Override
    @EventListener
    public void handleOutboundChannelDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info(headerAccessor.getSessionId() + ": " + headerAccessor.getMessage());
    }
}
