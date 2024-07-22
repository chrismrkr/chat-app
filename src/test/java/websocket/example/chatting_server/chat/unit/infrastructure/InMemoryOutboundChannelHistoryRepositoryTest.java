package websocket.example.chatting_server.chat.unit.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chat.infrastructure.impl.InMemoryOutboundChannelHistoryRepository;

import java.util.Map;

public class InMemoryOutboundChannelHistoryRepositoryTest {
    InMemoryOutboundChannelHistoryRepository repository;

    @BeforeEach
    void init() {
        repository = new InMemoryOutboundChannelHistoryRepository();
    }

    @Test
    void outboundChannel은_seq를_저장할_history를_생성할_수_있다() {
        // given
        String outboundChannelSessionId = "outbound.1";
        // when
        repository.createSessionHistory(outboundChannelSessionId);
        // then
        Map<String, Integer> history = repository.getHistory(outboundChannelSessionId);
        Assertions.assertNotNull(history);
    }

    @Test
    void outboundChannel은_seq를_업데이트할_수_있다() {
        // given
        String outboundChannelSessionId = "outbound.2";
        repository.createSessionHistory(outboundChannelSessionId);
        // when
        String inboundChannelSessionId = "inbound.1";
        int sequence = 0;
        repository.updateSequence(outboundChannelSessionId, inboundChannelSessionId, sequence);
        // then
        Assertions.assertEquals(sequence,
                repository.getSequence(outboundChannelSessionId, inboundChannelSessionId));
    }

    @Test
    void outboundSessionId로_sequence_History를_삭제할_수_있다() {
        // given
        String outboundChannelSessionId = "outbound.3";
        repository.createSessionHistory(outboundChannelSessionId);
        String inboundChannelSessionId = "inbound.2";
        int sequence = 0;
        repository.updateSequence(outboundChannelSessionId, inboundChannelSessionId, sequence);

        // when
        // then
        Assertions.assertDoesNotThrow( () ->
            repository.deleteSessionHistory(outboundChannelSessionId)
        );
    }

    @Test
    void 삭제한_것을_다시_해도_에러가_발생하지_않는다() {
        // given
        String outboundChannelSessionId = "outbound.4";
        // when
        // then
        Assertions.assertDoesNotThrow( () ->
                repository.deleteSessionHistory(outboundChannelSessionId)
        );
    }
}
