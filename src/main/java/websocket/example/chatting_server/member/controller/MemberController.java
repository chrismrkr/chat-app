package websocket.example.chatting_server.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import websocket.example.chatting_server.member.controller.dto.MemberCreateResDto;
import websocket.example.chatting_server.member.controller.port.MemberService;
import websocket.example.chatting_server.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@CrossOrigin(origins = "*")
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/{username}")
    public ResponseEntity<MemberCreateResDto> createMember(@PathVariable("username") String username) {
        Member member = memberService.create(username);
        return ResponseEntity.ok(new MemberCreateResDto(member.getId(), member.getMemberName()));
    }
}
