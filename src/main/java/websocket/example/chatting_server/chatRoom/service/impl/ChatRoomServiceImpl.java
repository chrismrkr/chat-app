package websocket.example.chatting_server.chatRoom.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    @Override
    public void delete(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow
                (() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId));
        chatRoomRepository.delete(chatRoom);
    }
    @Override
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
    @Override
    @Transactional
    public ChatRoom create(Long memberId, String roomName) {
        ChatRoom newChatRoom = chatRoomRepository.create(roomName);
        MemberChatRoom memberChatRoom = memberChatRoomRepository.addMemberInChatRoom(memberId, newChatRoom);
        return newChatRoom;
    }

    @Override
    @Transactional
    public MemberChatRoom enter(Long memberId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId));
        MemberChatRoom memberChatRoom = memberChatRoomRepository.addMemberInChatRoom(memberId, chatRoom);
        return memberChatRoom;
    }

    @Override
    public void exit(Long memberId, Long roomId) {
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndRoomId(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID]: ID NOT MATCHED"));
        memberChatRoomRepository.deleteMemberChatroomMapping(memberChatRoom);
        checkIsVacantRoom(roomId);
    }

    @Transactional
    private void checkIsVacantRoom(Long roomId) {
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(roomId);
        if(byRoomId == null || byRoomId.isEmpty()) {
            chatRoomRepository.delete(roomId);
        }
    }
}
