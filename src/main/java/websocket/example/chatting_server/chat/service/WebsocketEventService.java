package websocket.example.chatting_server.chat.service;

import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public interface WebsocketEventService {
    void handleOutboundChannelConnectedEvent(SessionConnectedEvent event);
    void handleOutboundChannelDisconnectEvent(SessionDisconnectEvent event);
}
