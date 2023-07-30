package com.example.service.repository.study;

import com.example.service.entity.study.Study;
import com.example.service.model.request.study.StudyControllerRequest;
import org.springframework.data.domain.Page;

public interface StudyRepositorySupport {

    Page<Study> findAllPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request);

}
