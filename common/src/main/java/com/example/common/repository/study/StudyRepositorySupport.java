package com.example.common.repository.study;

import com.example.common.entity.study.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRepositorySupport {

    Page<Study> findAllPublicStudyList(String name, String leaderId, Pageable pageable);

}
