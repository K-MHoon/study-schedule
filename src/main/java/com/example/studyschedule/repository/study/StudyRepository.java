package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositorySupport {

    Optional<Study> findByIdAndIsUse(Long studyId, IsUse isUse);

    Optional<Study> findByIdAndLeaderAndIsUse(Long studyId, Member leader, IsUse isUse);

    Optional<Study> findByIdAndSecretAndIsUse(Long studyId, Boolean secret, IsUse isUse);
}