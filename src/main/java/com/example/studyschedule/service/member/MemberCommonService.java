package com.example.studyschedule.service.member;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * @param memberId 회원 ID
     * @return 검증된 회원
     */
    @Transactional(readOnly = true)
    public Member validateExistedMemberId(Long memberId) {
        if(Objects.isNull(memberId)) {
            throw new IllegalArgumentException("스터디 회원 정보는 null일 수 없습니다.");
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ID에 해당하는 멤버를 찾을 수 없습니다. id = %d", memberId)));
    }

}
