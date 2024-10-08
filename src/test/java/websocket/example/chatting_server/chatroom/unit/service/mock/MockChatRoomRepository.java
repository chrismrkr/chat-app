package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MockChatRoomRepository implements ChatRoomRepository {
    private List<ChatRoom> datas = new ArrayList<>();
    private static AtomicLong ID_GEN = new AtomicLong(1L);

    @Override
    public Optional<ChatRoom> findByIdWithParticipants(Long roomId) {
        return datas.stream().filter(chatRoom -> chatRoom.getRoomId() == roomId)
                .findAny();
    }

    @Override
    public Optional<ChatRoom> findById(Long roomId) {
        return datas.stream().filter(chatRoom -> chatRoom.getRoomId() == roomId)
                .findAny();
    }

    @Override
    public List<ChatRoom> findAll() {
        return datas;
    }

    @Override
    public ChatRoom create(String roomName) {
        ChatRoom build = ChatRoom.builder()
                .roomId(ID_GEN.getAndIncrement())
                .roomName(roomName)
                .build();
        datas.add(build);
        return build;
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        datas.remove(chatRoom);
    }

    @Override
    public void delete(Long roomId) {
        ChatRoom chatRoom = findById(roomId).get();
        delete(chatRoom);
    }
}
