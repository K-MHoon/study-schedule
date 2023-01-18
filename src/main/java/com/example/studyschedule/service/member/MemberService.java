package com.example.studyschedule.service.member;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import com.example.studyschedule.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCommonService commonService;

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
        return MemberDto.entityToDto(commonService.validateExistedMemberId(memberId));
    }


    /**
     * 새로운 스터디 회원을 추가한다.
     *
     * @param request 신규 스터디 회원 정보를 가진 객체
     * @return 생성된 스터디 회원 Entity
     */
    @Transactional
    public Member createMember(MemberControllerRequest.CreateMemberRequest request) {
        Member newMember = new Member(request.getName(), request.getAge());
        return memberRepository.save(newMember);
    }

}
