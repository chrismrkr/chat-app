package websocket.example.chatting_server.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import websocket.example.chatting_server.member.controller.port.MemberService;
import websocket.example.chatting_server.member.domain.Member;
import websocket.example.chatting_server.member.service.port.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Override
    public Member create(String name) {
        return memberRepository.create(name);
    }
}
