package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Page<Study> findAllBySecretAndIsUse(Boolean secret, IsUse isUse, Pageable pageable);

    Optional<Study> findByIdAndLeaderAndIsUse(Long studyId, Member leader, IsUse isUse);

    Optional<Study> findByIdAndSecretAndIsUse(Long studyId, Boolean secret, IsUse isUse);
}