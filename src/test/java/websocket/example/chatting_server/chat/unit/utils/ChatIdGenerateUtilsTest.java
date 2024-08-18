package websocket.example.chatting_server.chat.unit.utils;

import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocket.example.chatting_server.chat.utils.ChatIdGenerateUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatIdGenerateUtilsTest {
    ChatIdGenerateUtils chatIdGenerateUtils;

    public ChatIdGenerateUtilsTest() {
        this.chatIdGenerateUtils = new ChatIdGenerateUtils(0, 0);
    }

    @Test
    void 멀티스레드_상황에서도_Unique_ID가_정상적으로_생성된다() throws InterruptedException {
        // given
        int THREAD_NUM = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
        CountDownLatch latch = new CountDownLatch(THREAD_NUM);
        Set<Long> idSet = new HashSet<>();
        // when
        for(int i=0; i<THREAD_NUM; i++) {
            try {
                executorService.submit(() -> {
                    long id = chatIdGenerateUtils.nextId();
                    idSet.add(id);
                });
            } finally {
                latch.countDown();
            }
        }
        latch.await();
        executorService.shutdown();
        // then
        Assertions.assertEquals(idSet.size(), THREAD_NUM);
    }

    @Test
    void Unique_ID를_timestamp와_serialNumber로_정렬할_수_있다() throws InterruptedException {
        // given
        long TIMESTAMP_MASK = (1L << 42) - 1; // 42 bits
        long SEQUENCE_MASK = (1L << 12) - 1; // 12 bits
        int TIMESTAMP_SHIFT = 12; // 시퀀스 번호 비트 수
        int THREAD_NUM = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
        CountDownLatch latch = new CountDownLatch(THREAD_NUM);
        List<TimestampAndSeq> idList = Collections.synchronizedList(new ArrayList<>()); // 동기화된 리스트 사용

        // when
        for (int i = 0; i < THREAD_NUM; i++) {
            executorService.submit(() -> {
                try {
                    long id = chatIdGenerateUtils.nextId();
                    long timeStamp = (id >> TIMESTAMP_SHIFT) & TIMESTAMP_MASK;
                    ZonedDateTime dateTime = getDateTime(timeStamp);
                    long sequence = id & SEQUENCE_MASK;
                    TimestampAndSeq timestampAndSeq = new TimestampAndSeq(dateTime, timeStamp, sequence);
                    idList.add(timestampAndSeq);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        idList.sort(new Comparator<TimestampAndSeq>() {
            @Override
            public int compare(TimestampAndSeq o1, TimestampAndSeq o2) {
                if(o1.time.isBefore(o2.time)) return -1;
                else if(o1.time.equals(o2.time)) {
                    if(o1.seq < o2.seq) return -1;
                    else return 1;
                } else return 1;
            }
        });
        Assertions.assertEquals(idList.size(), THREAD_NUM);
        for(int i=0; i<idList.size()-1; i++) {
            if(idList.get(i).time.equals(idList.get(i+1).time)) {
                Assertions.assertNotEquals(idList.get(i).seq, idList.get(i+1).seq);
            }
        }
    }

    private ZonedDateTime getDateTime(long timestamp) {
        long EPOCH = 1609459200000L;
        long epochMillis = EPOCH + timestamp;
        Instant instant = Instant.ofEpochMilli(epochMillis);
        return ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    private static class TimestampAndSeq {
        public ZonedDateTime time;
        public long timestamp;
        public long seq;
        public TimestampAndSeq(ZonedDateTime time, long timestamp, long seq) {
            this.time = time;
            this.timestamp = timestamp;
            this.seq = seq;
        }
    }
}
