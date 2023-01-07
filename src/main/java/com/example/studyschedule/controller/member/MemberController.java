package com.example.studyschedule.controller.member;

import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<MemberDto> getMemberList() {
        log.info("[getMemberList] call");

        List<MemberDto> response = memberService.getMemberList();

        return response;
    }

    /**
     * 특정 스터디 회원 정보를 가지고 온다.
     *
     * @param memberId 스터디 회원 ID
     * @return 스터디 회원 정보
     */
    @GetMapping("/{member_id}")
    public MemberDto getMember(@PathVariable(name = "member_id") Long memberId) {
        log.info("[getMember] call");

        MemberDto response = memberService.getMember(memberId);

        return response;
    }
}
