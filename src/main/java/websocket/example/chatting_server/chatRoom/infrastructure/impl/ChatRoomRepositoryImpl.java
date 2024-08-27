package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomJpaRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.ChatRoomEntity;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Override
    public Optional<ChatRoom> findByIdWithParticipants(Long roomId) {
        return chatRoomJpaRepository.findByIdWithChatRoom(roomId)
                .map(ChatRoom::from);
    }

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
    public ChatRoom create(String roomName) {
        ChatRoom newChatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();
        ChatRoomEntity save = chatRoomJpaRepository.save(newChatRoom.toEntity());
        return ChatRoom.from(save);
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        chatRoomJpaRepository.delete(chatRoom.toEntity());
    }

    @Override
    public void delete(Long roomId) {
        chatRoomJpaRepository.deleteById(roomId);
    }

}
