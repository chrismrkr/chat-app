package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockMemberChatRoomRepository implements MemberChatRoomRepository {
    private List<MemberChatRoom> datas = new ArrayList<>();


    @Override
    public MemberChatRoom save(MemberChatRoom memberChatRoom) {
        datas.add(memberChatRoom);
        return memberChatRoom;
    }

    @Override
    public void deleteMemberChatroomMapping(MemberChatRoom memberChatRoom) {
        datas.remove(memberChatRoom);
    }

    @Override
    public void deleteById(Long memberId, Long roomId) {
        Optional<MemberChatRoom> any = datas.stream()
                .filter(memberChatRoom -> memberChatRoom.getMemberId().equals(memberId) && memberChatRoom.getChatRoom().getRoomId().equals(roomId))
                .findAny();
        if(any.isPresent()) {
            datas.remove(any.get());
        }
    }

    @Override
    public Optional<MemberChatRoom> findByMemberAndRoomId(Long memberId, Long roomId) {
        Optional<MemberChatRoom> any = datas.stream()
                .filter(memberChatRoom ->
                        memberChatRoom.getMemberId() == memberId && memberChatRoom.getChatRoom().getRoomId() == roomId)
                .findAny();
        return any;
    }

    @Override
    public Optional<LocalDateTime> findEnterDateTime(Long memberId, Long roomId) {
        return datas.stream()
                .filter(memberChatRoom ->
                        memberChatRoom.getMemberId() == memberId && memberChatRoom.getChatRoom().getRoomId() == roomId)
                .findAny()
                .map(MemberChatRoom::getEnterDateTime);
    }

    @Override
    public List<MemberChatRoom> findByMemberId(Long memberId) {
        List<MemberChatRoom> list = datas.stream().filter(memberChatRoom -> memberChatRoom.getMemberId() == memberId)
                .toList();
        return list;
    }

    @Override
    public List<MemberChatRoom> findByRoomId(Long roomId) {
        List<MemberChatRoom> list = datas.stream().filter(memberChatRoom -> memberChatRoom.getChatRoom().getRoomId() == roomId)
                .toList();
        return list;
    }
}
