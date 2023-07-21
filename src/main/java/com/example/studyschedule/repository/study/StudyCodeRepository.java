package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyCodeRepository extends JpaRepository<StudyCode, Long> {

    @EntityGraph(attributePaths = {"study", "useMember"})
    Optional<StudyCode> findByInviteCodeAndUseMemberIsNull(String inviteCode);

    @EntityGraph(attributePaths = {"study", "useMember"})
    Optional<StudyCode> findByInviteCodeAndUseMember_Id(String inviteCode, Long useMemberId);

    @EntityGraph(attributePaths = {"useMember"})
    List<StudyCode> findAllByStudy(Study study);

    @EntityGraph(attributePaths = {"useMember"})
    List<StudyCode> findAllByStudyAndIdIn(Study study, List<Long> inviteCodeIdList);
}
