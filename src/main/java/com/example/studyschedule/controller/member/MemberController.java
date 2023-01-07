package com.example.studyschedule.controller.member;

import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.response.member.MemberControllerResponse;
import com.example.studyschedule.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @return
     */
    @GetMapping
    public List<MemberDto> getMemberList() {
        log.info("[getMemberList] call");

        List<MemberDto> response = memberService.getMemberList();

        return response;
    }
}
