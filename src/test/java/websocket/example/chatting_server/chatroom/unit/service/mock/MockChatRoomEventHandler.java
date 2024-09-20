package websocket.example.chatting_server.chatroom.unit.service.mock;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import websocket.example.chatting_server.chatRoom.domain.MemberChatRoom;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomEventHandler;
import websocket.example.chatting_server.chatRoom.infrastructure.ChatRoomRepository;
import websocket.example.chatting_server.chatRoom.infrastructure.MemberChatRoomRepository;
import websocket.example.chatting_server.chatRoom.service.event.ChatRoomExitEvent;

import java.util.List;

public class MockChatRoomEventHandler implements ChatRoomEventHandler {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;

    public MockChatRoomEventHandler(ChatRoomRepository chatRoomRepository, MemberChatRoomRepository memberChatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.memberChatRoomRepository = memberChatRoomRepository;
    }
    @Override
    public void publishEmptyCheck(ChatRoomExitEvent chatRoomExitEvent) {
        Long roomId = chatRoomExitEvent.getRoomId();
        List<MemberChatRoom> byRoomId = memberChatRoomRepository.findByRoomId(roomId);
        if(byRoomId.isEmpty()) {
            chatRoomRepository.delete(roomId);
        }
    }

    @Override
    public void subscribeEmptyCheck(ConsumerRecord<Long, Long> record, Acknowledgment acknowledgment) {

    }
}
