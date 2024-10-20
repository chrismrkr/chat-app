package websocket.example.chatting_server.chat.infrastructure;

public interface LockRepository {
    boolean holdLock(String key, String value);
    void releaseLock(String key, String value);
    boolean isLocked(String key);
}
