package websocket.example.chatting_server.chatRoom.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chat.domain.ChatHistory;
import websocket.example.chatting_server.chat.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chat.service.ChatHistoryService;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
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
    public List<ChatRoom> findByMemberId(Long memberId) {
        return memberChatRoomRepository.findByMemberId(memberId)
                .stream()
                .map(MemberChatRoom::getChatRoom)
                .toList();
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

    @Override
    public List<ChatHistory> readChatHistory(Long memberId, Long roomId) {
        LocalDateTime enterDateTime = memberChatRoomRepository.findEnterDateTime(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID] NOT FOUND"));
        return chatHistoryRepository.findByRoomIdAndSendTimeAfter(roomId, enterDateTime);
    }

    @Transactional
    private void checkIsVacantRoom(Long roomId) {
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(roomId);
        if(byRoomId == null || byRoomId.isEmpty()) {
            chatRoomRepository.delete(roomId);
        }
    }
}
