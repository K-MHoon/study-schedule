package com.example.service.repository.study;

import com.example.service.entity.member.Member;
import com.example.service.entity.study.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long>, StudyMemberRepositorySupport {

    List<StudyMember> findAllByStudy_IdInAndMember_Id(List<Long> studyIdList, Long memberId);
    Optional<StudyMember> findByStudy_IdAndMember_Id(Long studyId, Long memberId);
    boolean existsStudyMemberByStudy_IdAndMember_Id(Long studyId, Long memberId);
    boolean existsByMember(Member member);
}
