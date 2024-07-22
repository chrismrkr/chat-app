package websocket.example.chatting_server.chat.infrastructure;

import java.util.Map;

public interface OutboundChannelHistoryRepository {
    void createSessionHistory(String outboundSessionId);
    void deleteSessionHistory(String outboundSessionId);
    void updateSequence(String outboundSessionId, String inboundSessionId, Integer seq);
    int getSequence(String outboundSessionId, String inboundSessionId);
    Map<String, Integer> getHistory(String outboundSessionId);
}
