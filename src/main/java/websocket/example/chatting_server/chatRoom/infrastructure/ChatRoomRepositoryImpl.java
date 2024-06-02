package websocket.example.chatting_server.chatRoom.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;
import websocket.example.chatting_server.chatRoom.service.port.ChatRoomRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    @Override
    public Optional<ChatRoom> findById(Long roomId) {
        return chatRoomJpaRepository.findById(roomId)
                .map(ChatRoom::from);
    }
    @Override
    public List<ChatRoom> findAll() {
        return chatRoomJpaRepository.findAll()
                .stream().map(ChatRoom::from)
                .toList();
    }
    @Override
    public ChatRoom create(Long memberId, String roomName) {
        ChatRoom newChatRoom = ChatRoom.builder()
                .roomName(roomName)
                .memberId(memberId)
                .build();
        ChatRoomEntity save = chatRoomJpaRepository.save(newChatRoom.toEntity());
        return ChatRoom.from(save);
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        chatRoomJpaRepository.delete(chatRoom.toEntity());
    }
}
