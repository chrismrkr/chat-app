package websocket.example.chatting_server.chat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;
import websocket.example.chatting_server.chat.service.WebsocketEventService;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventServiceImpl implements WebsocketEventService {
    private final OutboundChannelHistoryRepository outboundChannelHistoryRepository;
    @Override
    @EventListener
    public void handleOutboundChannelConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info(accessor.getSessionId() + ": " + accessor.getMessage());
        outboundChannelHistoryRepository.createSessionHistory(accessor.getSessionId());
    }

    @Override
    @EventListener
    public void handleOutboundChannelDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info(accessor.getSessionId() + ": " + accessor.getMessage());
        outboundChannelHistoryRepository.deleteSessionHistory(accessor.getSessionId());
    }
}
