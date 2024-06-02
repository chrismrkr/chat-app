package websocket.example.chatting_server.member.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity(name = "member")
@Getter
public class MemberEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String memberName;

    @Builder
    public MemberEntity(Long id, String memberName) {
        this.id = id;
        this.memberName = memberName;
    }
}
