package websocket.example.chatting_server.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import websocket.example.chatting_server.member.infrastructure.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}
