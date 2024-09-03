package websocket.example.chatting_server.chatRoom.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatHistoryRepository;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ChatRoom create(Long memberId, String roomName) {
        ChatRoom newChatRoom = chatRoomRepository.create(roomName);
        MemberChatRoom participate = newChatRoom.participate(memberId);
        memberChatRoomRepository.save(participate);
        return newChatRoom;
    }

    @Override
    @Transactional
    public MemberChatRoom enter(Long memberId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId));
        MemberChatRoom participate = chatRoom.participate(memberId);
        participate = memberChatRoomRepository.save(participate);
        return participate;
    }

    @Override
    @Transactional
    public void exit(Long memberId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + roomId));
        memberChatRoomRepository.deleteById(memberId, roomId);
        List<MemberChatRoom> roomParticipants = memberChatRoomRepository.findByRoomId(roomId);
        if(roomParticipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    @Override
    public ChatHistory writeChatHistory(ChatDto chatDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatDto.getRoomId()).orElseThrow
                (() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + chatDto.getRoomId()));
        ChatHistory chatHistory = chatRoom.createChatHistory(chatDto);
        chatHistory = chatHistoryRepository.save(chatHistory);
        return chatHistory;
    }

    @Override
    public List<ChatHistory> readChatHistory(Long memberId, Long roomId) {
        LocalDateTime enterDateTime = memberChatRoomRepository.findEnterDateTime(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID] NOT FOUND"));
        return chatHistoryRepository.findByRoomIdAndSendTimeAfter(roomId, enterDateTime);
    }
}
