package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.StudyCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyCodeRepository extends JpaRepository<StudyCode, Long> {

    @EntityGraph(attributePaths = {"study", "useMember"})
    Optional<StudyCode> findByInviteCodeAndUseMemberIsNull(String inviteCode);

    @EntityGraph(attributePaths = {"study", "useMember"})
    Optional<StudyCode> findByInviteCodeAndUseMember_Id(String inviteCode, Long useMemberId);
}
