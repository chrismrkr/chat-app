package websocket.example.chatting_server.chatroom.unit.service.mock;

import websocket.example.chatting_server.chatRoom.domain.ChatRoom;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockMemberChatRoomRepository implements MemberChatRoomRepository {
    private List<MemberChatRoom> datas = new ArrayList<>();
    @Override
    public MemberChatRoom addMemberInChatRoom(Long memberId, ChatRoom chatRoom) {
        MemberChatRoom build = MemberChatRoom.builder()
                .memberId(memberId)
                .chatRoom(chatRoom)
                .build();
        datas.add(build);
        return build;
    }

    @Override
    public void deleteMemberChatroomMapping(MemberChatRoom memberChatRoom) {
        datas.remove(memberChatRoom);
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
