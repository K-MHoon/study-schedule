package com.example.service.controller.member;

import com.example.common.model.dto.member.MemberDto;
import com.example.common.model.dto.security.TokenInfo;
import com.example.service.controller.request.member.MemberControllerRequest;
import com.example.service.service.member.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    /**
     * 스터디 전체 회원 정보를 가지고 온다.
     *
     * @return 스터디 전체 회원 정보
     */
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberDto> getMemberList() {
        return memberService.getMemberList();
    }

    @GetMapping("/member/profile")
    @ResponseStatus(HttpStatus.OK)
    public MemberDto getMemberProfile() {
        return memberService.getMemberProfile();
    }

    /**
     * 특정 스터디 회원 정보를 가지고 온다.
     *
     * @param id 스터디 회원 ID
     * @return 스터디 회원 정보
     */
    @GetMapping("/member/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberDto getMemberById(@PathVariable(name = "id") Long id) {
        return memberService.getMemberById(id);
    }

    /**
     * 신규 스터디 회원을 추가한다.
     *
     * @param request 요청된 스터디 회원의 정보
     * @return 정상적으로 생성된 경우 OK
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void createMember(@RequestBody @Validated MemberControllerRequest.CreateMember request) {
        memberService.createMember(request.toServiceRequest());
    }

    @PostMapping("/member/profile")
    @ResponseStatus(HttpStatus.OK)
    public void updateMemberProfile(@RequestBody @Validated MemberControllerRequest.UpdateMemberProfile request) {
        memberService.updateMemberProfile(request.toServiceRequest());
    }

    @DeleteMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMember() {
        memberService.deleteMember();
    }
}
