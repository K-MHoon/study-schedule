package com.example.common.repository.study;

import com.example.common.enums.IsUse;
import com.example.common.entity.member.Member;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositorySupport {

    Optional<Study> findByIdAndIsUse(Long studyId, IsUse isUse);

    Optional<Study> findByIdAndLeaderAndIsUse(Long studyId, Member leader, IsUse isUse);

    Optional<Study> findByIdAndSecretAndIsUse(Long studyId, Boolean secret, IsUse isUse);

    boolean existsByLeader(Member member);
}