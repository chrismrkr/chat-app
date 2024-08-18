package websocket.example.chatting_server.chat.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chat.infrastructure.OutboundChannelHistoryRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryOutboundChannelHistoryRepository implements OutboundChannelHistoryRepository {
    private Map<String, ConcurrentHashMap<String, Long>> sequenceHistoryStorages = new HashMap<>();
//    private Map<String, ConcurrentHashMap<String, Boolean>> sessionLockStorages = new HashMap<>();
    @Override
    public void createSessionHistory(String receiverSessionId) {
        ConcurrentHashMap<String, Long> seqHistory = new ConcurrentHashMap<>();
        sequenceHistoryStorages.put(receiverSessionId, seqHistory);
    }

    @Override
    public void deleteSessionHistory(String receiverSessionId) {
        if(!sequenceHistoryStorages.containsKey(receiverSessionId)) {
            return;
        }
        Map<String, Long> seqHistory = sequenceHistoryStorages.get(receiverSessionId);
        for(String senderSessionId : seqHistory.keySet()) {
            seqHistory.remove(senderSessionId);
        }
        sequenceHistoryStorages.remove(receiverSessionId);
    }

    @Override
    public void updateSequence(String receiverSessionId, String senderSessionId, Long seq) {
        Map<String, Long> seqHistory = sequenceHistoryStorages.get(receiverSessionId);
        log.info("[SESSION HISTORY UPDATE] " + receiverSessionId + " <-- " + senderSessionId + ": #" + seq);
        seqHistory.put(senderSessionId, seq);
    }

    @Override
    public long getSequence(String receiverSessionId, String senderSessionId) {
        Long seq = sequenceHistoryStorages.get(receiverSessionId).get(senderSessionId);
        return seq;
    }

    @Override
    public Map<String, Long> getHistory(String outboundSessionId) {
        return sequenceHistoryStorages.get(outboundSessionId);
    }
    @Override
    public boolean isSenderSessionExists(String receiverSessionId, String senderSessionId) {
        Map<String, Long> senderSessionsHistory = sequenceHistoryStorages.get(receiverSessionId);
        if(senderSessionsHistory.containsKey(senderSessionId)) {
            return true;
        }
        return false;
    }
}
