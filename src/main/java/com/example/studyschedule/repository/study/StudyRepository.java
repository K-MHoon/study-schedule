package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Page<Study> findAllBySecretAndIsUse(Boolean secret, IsUse isUse, Pageable pageable);
}