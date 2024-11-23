package websocket.example.chatting_server.chatRoom.service.inner.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.inner.ChatRoomInnerService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomInnerServiceImpl implements ChatRoomInnerService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ChatRoom exit(Long memberId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(
                        () -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId)
                );
        memberChatRoomRepository.deleteById(memberId, roomId);
        return chatRoom;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkEmpty(Long roomId) {
        List<MemberChatRoom> participants = memberChatRoomRepository.findByRoomId(roomId);
        if(!participants.isEmpty())
            return;
        try {
            chatRoomRepository.delete(roomId);
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] CHATROOM DELETE FAILED");
        }
    }
}
