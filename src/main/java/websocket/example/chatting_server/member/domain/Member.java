package websocket.example.chatting_server.member.domain;


import lombok.Builder;
import lombok.Getter;
import websocket.example.chatting_server.member.infrastructure.entity.MemberEntity;

@Getter
public class Member {
    private Long id;
    private String memberName;
    @Builder
    public Member(Long id, String memberName) {
        this.id = id;
        this.memberName = memberName;
    }
    public static Member from(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .memberName(entity.getMemberName())
                .build();
    }
    public MemberEntity to() {
        return MemberEntity
                .builder()
                .id(this.id)
                .memberName(this.memberName)
                .build();
    }
}
