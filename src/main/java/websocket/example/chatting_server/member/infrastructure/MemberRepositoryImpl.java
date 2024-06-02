package websocket.example.chatting_server.member.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import websocket.example.chatting_server.member.domain.Member;
import websocket.example.chatting_server.member.infrastructure.entity.MemberEntity;
import websocket.example.chatting_server.member.service.port.MemberRepository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;
    @Override
    public Member create(String username) {
        MemberEntity build = MemberEntity.builder()
                .memberName(username)
                .build();
        MemberEntity save = memberJpaRepository.save(build);
        return Member.from(save);
    }
}
