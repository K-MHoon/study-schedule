package com.example.service.service.study;

import com.example.common.entity.member.Member;
import com.example.common.entity.study.StudyMember;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.service.service.member.MemberCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyCommonService {

    private final MemberCommonService memberCommonService;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional(readOnly = true)
    public StudyMember getMyStudyMember(Long studyId) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        StudyMember studyMember = studyMemberRepository.findMyStudyMember(studyId, loggedInMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않거나 가입되지 않은 스터디 입니다."));
        if (!studyMember.getStudy().getLeader().equals(loggedInMember)) {
            throw new IllegalArgumentException("본인의 스터디가 아닙니다.");
        }
        return studyMember;
    }
}
