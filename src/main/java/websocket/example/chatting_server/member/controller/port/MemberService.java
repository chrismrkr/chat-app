package websocket.example.chatting_server.member.controller.port;

import websocket.example.chatting_server.member.domain.Member;

public interface MemberService {
    Member create(String name);
}
