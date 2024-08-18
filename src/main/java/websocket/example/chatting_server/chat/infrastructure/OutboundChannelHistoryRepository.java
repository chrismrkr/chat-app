package websocket.example.chatting_server.chat.infrastructure;

import java.util.Map;

public interface OutboundChannelHistoryRepository {
    void createSessionHistory(String outboundSessionId);
    void deleteSessionHistory(String outboundSessionId);
    void updateSequence(String outboundSessionId, String inboundSessionId, Long seq);
    long getSequence(String outboundSessionId, String inboundSessionId);
    Map<String, Long> getHistory(String outboundSessionId);
    boolean isSenderSessionExists(String receiverSessionId, String senderSessionIdx);
}
