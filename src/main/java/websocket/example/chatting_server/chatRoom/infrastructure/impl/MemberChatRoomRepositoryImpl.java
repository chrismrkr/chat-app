package websocket.example.chatting_server.chatRoom.infrastructure.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomJpaRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.MemberChatRoomEntity;
import websocket.example.chatting_server.chatRoom.infrastructure.entity.compositeKey.MemberChatRoomId;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberChatRoomRepositoryImpl implements MemberChatRoomRepository {
    private final MemberChatRoomJpaRepository memberChatRoomJpaRepository;

    @Override
    public MemberChatRoom addMemberInChatRoom(Long memberId, ChatRoom chatRoom) {
        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .memberId(memberId)
                .chatRoom(chatRoom)
                .build();
        MemberChatRoomEntity save = memberChatRoomJpaRepository.save(memberChatRoom.toEntity());
        return MemberChatRoom.from(save);
    }

    @Override
    public void deleteMemberChatroomMapping(MemberChatRoom memberChatRoom) {
        memberChatRoomJpaRepository.delete(memberChatRoom.toEntity());
    }


    @Override
    public List<MemberChatRoom> findByMemberId(Long memberId) {
        List<MemberChatRoom> list = memberChatRoomJpaRepository.findByMemberIdWithChatRoom(memberId)
                .stream().map(MemberChatRoom::from)
                .toList();
        return list;
    }

    @Override
    public List<MemberChatRoom> findByRoomId(Long roomId) {
        List<MemberChatRoom> list = memberChatRoomJpaRepository.findByRoomIdWithChatRoom(roomId)
                .stream().map(MemberChatRoom::from)
                .toList();
        return list;
    }

    @Override
    @Transactional
    public Optional<MemberChatRoom> findByMemberAndRoomId(Long memberId, Long roomId) {
        Optional<MemberChatRoom> memberChatRoom = memberChatRoomJpaRepository.findById(new MemberChatRoomId(memberId, roomId))
                .map(MemberChatRoom::from);
        return memberChatRoom;
    }
}
