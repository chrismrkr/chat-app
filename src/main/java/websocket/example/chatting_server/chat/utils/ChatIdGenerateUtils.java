package websocket.example.chatting_server.chat.utils;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ChatIdGenerateUtils {
    // Use Snowflake ID Generator
    private final long twepoch = 1288834974657L; // Epoch start timestamp
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;
    public static final long TIMESTAMP_MASK = (1L << 42) - 1; // 42 bits
    public static final long SEQUENCE_MASK = (1L << 12) - 1; // 12 bits
    public static final int TIMESTAMP_SHIFT = 12; // 시퀀스 번호 비트 수

    // bit shifts
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public ChatIdGenerateUtils(long workerId, long datacenterId) {
        if (workerId > (1L << workerIdBits) - 1 || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", (1L << workerIdBits) - 1));
        }
        if (datacenterId > (1L << datacenterIdBits) - 1 || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", (1L << datacenterIdBits) - 1));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (timestamp == lastTimestamp) {
            // 1ms 내에 ID 신규 생성
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 1ms 내 2^12=4096개 넘게 ID가 생성된 경우
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 64bit
        return ((timestamp - twepoch) << timestampLeftShift) // 64 << 22 -> 42bit
                | (datacenterId << datacenterIdShift) // 64 << 17 -> 47 -> 5bit
                | (workerId << workerIdShift) // 64 << 12 -> 52 -> 5bit
                | sequence; // 12bit
    }

    public static long getTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) & TIMESTAMP_MASK;
    }
    public static long getSerial(long id) {
        return id & SEQUENCE_MASK;
    }
    public static ZonedDateTime getDateTime(long timestamp) {
        long epochMillis = 1288834974657L + timestamp;
        Instant instant = Instant.ofEpochMilli(epochMillis);
        return ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
