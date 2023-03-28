package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    List<StudyMember> findAllByStudy_IdInAndMember_Id(List<Long> studyIdList, Long memberId);
    boolean existsStudyMemberByStudy_IdAndMember_Id(Long studyId, Long memberId);
}
