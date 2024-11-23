package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.inner.ChatRoomInnerService;

import java.util.List;

public class MockChatRoomInnerService implements ChatRoomInnerService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    public MockChatRoomInnerService(ChatRoomRepository chatRoomRepository, MemberChatRoomRepository memberChatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberChatRoomRepository = memberChatRoomRepository;
    }

    @Override
    public ChatRoom exit(Long memberId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(
                        () -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId)
                );
        memberChatRoomRepository.deleteById(memberId, roomId);
        return chatRoom;
    }

    @Override
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
