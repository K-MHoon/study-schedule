package com.example.studyschedule.controller.member;

import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import com.example.studyschedule.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 스터디 전체 회원 정보를 가지고 온다.
     *
     * @return 스터디 전체 회원 정보
     */
    @GetMapping
    public ResponseEntity<List<MemberDto>> getMemberList() {
        log.info("[getMemberList] call");

        List<MemberDto> response = memberService.getMemberList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 스터디 회원 정보를 가지고 온다.
     *
     * @param memberId 스터디 회원 ID
     * @return 스터디 회원 정보
     */
    @GetMapping("/{member_id}")
    public ResponseEntity<MemberDto> getMember(@PathVariable(name = "member_id") Long memberId) {
        log.info("[getMember] call");

        MemberDto response = memberService.getMember(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 신규 스터디 회원을 추가한다.
     *
     * @param request 요청된 스터디 회원의 정보
     * @return 정상적으로 생성된 경우 OK
     */
    @PostMapping
    public ResponseEntity createMember(@RequestBody @Valid MemberControllerRequest.CreateMemberRequest request) {
        log.info("[createMember] call");

        memberService.createMember(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
