package websocket.example.chatting_server.chat.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryOutboundChannelHistoryRepository implements OutboundChannelHistoryRepository {
    private Map<String, Map<String, Integer>> sessionHistoryStorages = new HashMap<>();
    @Override
    public void createSessionHistory(String outboundSessionId) {
        Map<String, Integer> seqHistory = new HashMap<>();
        sessionHistoryStorages.put(outboundSessionId, seqHistory);
    }

    @Override
    public void deleteSessionHistory(String outboundSessionId) {
        if(!sessionHistoryStorages.containsKey(outboundSessionId)) {
            return;
        }
        Map<String, Integer> seqHistory = sessionHistoryStorages.get(outboundSessionId);
        for(String inboundSessionId : seqHistory.keySet()) {
            seqHistory.remove(inboundSessionId);
        }
        sessionHistoryStorages.remove(outboundSessionId);
    }

    @Override
    public void updateSequence(String outboundSessionId, String inboundSessionId, Integer seq) {
        Map<String, Integer> seqHistory = sessionHistoryStorages.get(outboundSessionId);
        seqHistory.put(inboundSessionId, seq);
    }

    @Override
    public int getSequence(String outboundSessionId, String inboundSessionId) {
        Integer seq = sessionHistoryStorages.get(outboundSessionId).get(inboundSessionId);
        return seq;
    }

    @Override
    public Map<String, Integer> getHistory(String outboundSessionId) {
        return sessionHistoryStorages.get(outboundSessionId);
    }
}
