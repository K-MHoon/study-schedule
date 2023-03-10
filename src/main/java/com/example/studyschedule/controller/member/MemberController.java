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
    public ResponseEntity<List<MemberDto>> getMemberList() {
        log.info("[getMemberList] call");

        List<MemberDto> response = memberService.getMemberList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/member/profile")
    public ResponseEntity<MemberDto> getMemberProfile(Principal principal) {
        log.info("[getMemberProfile] called by = {}", principal.getName());

        MemberDto response = memberService.getMemberProfile();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 스터디 회원 정보를 가지고 온다.
     *
     * @param id 스터디 회원 ID
     * @return 스터디 회원 정보
     */
    @GetMapping("/member/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable(name = "id") Long id, Principal principal) {
        log.info("[getMemberById] called by = {}", principal.getName());

        MemberDto response = memberService.getMemberById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 신규 스터디 회원을 추가한다.
     *
     * @param request 요청된 스터디 회원의 정보
     * @return 정상적으로 생성된 경우 OK
     */
    @PostMapping("/register")
    public ResponseEntity createMember(@RequestBody @Valid MemberControllerRequest.CreateMemberRequest request) {
        log.info("[createMember] ID = {}, name = {}, age = {}", request.getMemberId(), request.getName(), request.getAge());

        memberService.createMember(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 로그인을 한다.
     *
     * @param request 로그인 정보
     * @return {@link TokenInfo 로그인 토큰 정보}
     */
    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody @Valid MemberControllerRequest.LoginRequest request) {
        log.info("[login] Id = {}", request.getMemberId());

        TokenInfo response = memberService.login(request.getMemberId(), request.getPassword());

        return new ResponseEntity<>(response, HttpStatus.OK);
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
