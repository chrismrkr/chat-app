package websocket.example.chatting_server.chatRoom.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chat.controller.dto.ChatDto;
import websocket.example.chatting_server.chatRoom.aop.annotation.ChatRoomHistoryLock;
import websocket.example.chatting_server.chatRoom.domain.ChatHistory;
import websocket.example.chatting_server.chatRoom.infrastructure.*;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.service.ChatRoomService;
import websocket.example.chatting_server.chatRoom.service.inner.ChatRoomInnerService;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatRoomCacheRepository chatRoomCacheRepository;
    private final ChatRoomInnerService chatRoomInnerService;

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
        if(memberChatRoomRepository.findByRoomId(roomId).isEmpty()) {
            throw new IllegalArgumentException("[INVALID ROOM ID]: ROOM " + roomId + "ALREADY CLOSED");
        }
        MemberChatRoom participate = chatRoom.participate(memberId);
        participate = memberChatRoomRepository.save(participate);
        return participate;
    }

    @Override
    public void exit(Long memberId, Long roomId) {
        ChatRoom exit = chatRoomInnerService.exit(memberId, roomId);
        try {
            chatRoomInnerService.checkEmpty(roomId);
        } catch (RuntimeException exception) { // RollBack
            Optional<ChatRoom> byId = chatRoomRepository.findById(exit.getRoomId());
            if(byId.isPresent()) {
                memberChatRoomRepository.save(MemberChatRoom.builder()
                        .chatRoom(exit)
                        .memberId(memberId)
                        .build());
            }
        }
    }

    @Override
    @ChatRoomHistoryLock
    public ChatHistory writeChatHistory(Long roomId, ChatDto chatDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatDto.getRoomId()).orElseThrow
                (() -> new IllegalArgumentException("[INVALID ROOM ID]: ROOM NOT FOUND BY " + chatDto.getRoomId()));
        ChatHistory chatHistory = chatRoom.createChatHistory(chatDto);
        try {
            chatHistory = chatRoomCacheRepository.writeChatHistory(roomId, chatHistory);
            chatHistory = chatHistoryRepository.save(chatHistory);
        } catch (Exception e) {
            chatHistoryRepository.deleteBySeq(chatHistory.getSeq());
            chatRoomCacheRepository.deleteChatHistory(roomId, chatHistory.getSeq());
            throw e;
        }
        return chatHistory;
    }

    @Override
    @ChatRoomHistoryLock
    public List<ChatHistory> readChatHistory(Long roomId, Long memberId) {
        LocalDateTime enterDateTime = memberChatRoomRepository.findEnterDateTime(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID] NOT FOUND"));
        return chatHistoryRepository.findByRoomIdAndSendTimeAfter(roomId, enterDateTime);
    }

    @Override
    @ChatRoomHistoryLock
    public List<ChatHistory> readChatHistory(Long roomId, Long memberId, long currentSeq, int size) {
        LocalDateTime enterDateTime = memberChatRoomRepository.findEnterDateTime(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID] NOT FOUND"));
        return chatHistoryRepository.findByRoomIdAndSeqLessThanAndSendTimeAfter(roomId, currentSeq, enterDateTime, size);
    }

    @Override
    @ChatRoomHistoryLock
    public List<ChatHistory> readChatHistoryCache(Long roomId, Long memberId) {
        LocalDateTime enterDateTime = memberChatRoomRepository.findEnterDateTime(memberId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("[INVALID MEMBER OR ROOM ID] NOT FOUND"));
        return chatRoomCacheRepository.readChatHistory(roomId)
                .stream().filter(history -> history.getSendTime().isAfter(enterDateTime))
                .toList();
    }
}
