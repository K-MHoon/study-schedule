package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import org.springframework.data.domain.Page;

public interface StudyRepositorySupport {

    Page<Study> findAllPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request);

}
