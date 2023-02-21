package com.example.studyschedule.service.member;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.dto.security.TokenInfo;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import com.example.studyschedule.repository.member.MemberRepository;
import com.example.studyschedule.security.provider.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCommonService commonService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenInfo login(String memberId, String password) {
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(memberId, password));
        return jwtTokenProvider.generateToken(authentication);
    }

    /**
     * 스터디 전체 회원 정보를 가지고 온다.
     *
     * @return 스터디 회원 정보 목록
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMemberList() {
        return memberRepository.findAll().stream()
                .map(MemberDto::entityToDto)
                .collect(Collectors.toList());
    }


    /**
     * 스터디 단일 회원 정보를 가지고 온다.
     *
     * @param memberId 스터디 회원 ID
     * @return 단일 스터디 회원 정보
     */
    @Transactional(readOnly = true)
    public MemberDto getMember(Long memberId) {
        return MemberDto.entityToDto(commonService.validateExistedMemberById(memberId));
    }


    /**
     * 새로운 스터디 회원을 추가한다.
     *
     * @param request 신규 스터디 회원 정보를 가진 객체
     */
    @Transactional
    public void createMember(MemberControllerRequest.CreateMemberRequest request) {
        memberRepository.findByMemberId(request.getMemberId())
                .ifPresent(member -> {
                    log.error("동일한 멤버가 존재합니다. ID = {}", member.getMemberId());
                    throw new IllegalArgumentException("동일한 멤버가 존재합니다. ID = " + member.getMemberId());
                });

        Member newMember = new Member(request.getMemberId(), passwordEncoder.encode(request.getPassword()), List.of("USER"), request.getName(), request.getAge());
        memberRepository.save(newMember);
    }

    public ClientAuthInfo tokenCheck(Cookie[] cookies) {
        Map<String, String> cookieMap = Arrays.stream(cookies)
                .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie.getValue()));

        if(!cookieMap.containsKey("access_token")) {
            return new ClientAuthInfo(false);
        }

        if(jwtTokenProvider.validateToken(cookieMap.get("access_token"))){
            return new ClientAuthInfo(true, false, null);
        }

        if(!cookieMap.containsKey("refresh_token")) {
            return new ClientAuthInfo(false);
        }

        if(jwtTokenProvider.validateToken(cookieMap.get("refresh_token"))) {
            TokenInfo newToken = jwtTokenProvider.generateToken(jwtTokenProvider.getAuthentication(cookieMap.get("accessToken")));
            return new ClientAuthInfo(true, true, newToken);
        }

        return new ClientAuthInfo(false);
    }

    @Getter
    public static class ClientAuthInfo {

        private boolean auth;
        private boolean refresh;
        private TokenInfo tokenInfo;

        public ClientAuthInfo(boolean auth) {
            this.auth = auth;
        }

        public ClientAuthInfo(boolean auth, boolean refresh, TokenInfo tokenInfo) {
            this.auth = auth;
            this.refresh = refresh;
            this.tokenInfo = tokenInfo;
        }
    }
}
