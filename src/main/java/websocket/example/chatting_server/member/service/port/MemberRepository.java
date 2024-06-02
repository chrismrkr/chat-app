package websocket.example.chatting_server.member.service.port;

import websocket.example.chatting_server.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Member create(String username);
}
