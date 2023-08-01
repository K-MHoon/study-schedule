package com.example.service.service.member;

import com.example.common.entity.member.Member;
import com.example.common.repository.member.MemberRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberCommonService {

    private final MemberRepository memberRepository;

    /**
     * 회원 ID가 정상적인 스터디 회원인지 검증한다.
     *
     * @param id 회원 ID
     * @return 검증된 회원
     */
    @Transactional(readOnly = true)
    public Member validateExistedMemberById(Long id) {
        if(Objects.isNull(id)) {
            throw new IllegalArgumentException("스터디 회원 정보는 null일 수 없습니다.");
        }
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ID에 해당하는 멤버를 찾을 수 없습니다. id = %d", id)));
    }

    @Transactional(readOnly = true)
    public Member getLoggedInMember() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(StringUtils.isEmpty(userName)) {
            throw new IllegalArgumentException("로그인한 사용자가 존재하지 않습니다.");
        }
        return memberRepository.findByMemberId(userName)
                .orElseThrow(() -> new EntityNotFoundException(String.format("member ID에 해당하는 멤버를 찾을 수 없습니다. memberId = %s", userName)));
    }

}
