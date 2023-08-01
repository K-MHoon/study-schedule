package com.example.common.repository.study;

import com.example.common.model.request.study.StudyControllerRequest;
import com.example.common.entity.study.Study;
import com.example.common.model.request.study.StudyControllerRequest;
import org.springframework.data.domain.Page;

public interface StudyRepositorySupport {

    Page<Study> findAllPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request);

}
