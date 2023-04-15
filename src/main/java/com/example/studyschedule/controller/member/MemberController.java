package com.example.studyschedule.controller.member;

import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.dto.security.TokenInfo;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import com.example.studyschedule.service.member.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
        log.info("[getMemberList] call");

        return memberService.getMemberList();
    }

    @GetMapping("/member/profile")
    @ResponseStatus(HttpStatus.OK)
    public MemberDto getMemberProfile(Principal principal) {
        log.info("[getMemberProfile] called by {}", principal.getName());

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
    public MemberDto getMemberById(@PathVariable(name = "id") Long id, Principal principal) {
        log.info("[getMemberById] called by {}", principal.getName());

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
    public void createMember(@RequestBody @Valid MemberControllerRequest.CreateMemberRequest request) {
        log.info("[createMember] ID = {}, name = {}, age = {}", request.getMemberId(), request.getName(), request.getAge());

        memberService.createMember(request);
    }

    /**
     * 로그인을 한다.
     *
     * @param request 로그인 정보
     * @return {@link TokenInfo 로그인 토큰 정보}
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenInfo login(@RequestBody @Valid MemberControllerRequest.LoginRequest request) {
        log.info("[login] Id = {}", request.getMemberId());

        return memberService.login(request.getMemberId(), request.getPassword());
    }

    @PostMapping("/member/profile")
    @ResponseStatus(HttpStatus.OK)
    public void updateMemberProfile(@RequestBody MemberControllerRequest.UpdateMemberProfileRequest request,
                                    Principal principal) {
        log.info("[updateMemberProfile] called by {}, request = {}", principal.getName(), request);

        memberService.updateMemberProfile(request);
    }

    @PostMapping("/token/check")
    public ResponseEntity<TokenInfo> tokenCheck(HttpServletRequest request) {
        log.info("[tokenCheck] = request");

        Cookie[] cookies = request.getCookies();

        if(cookies == null || cookies.length < 2) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        MemberService.ClientAuthInfo response = memberService.tokenCheck(cookies);

        if(!response.isAuth()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!response.isRefresh()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(response.getTokenInfo(), HttpStatus.OK);
    }
}
