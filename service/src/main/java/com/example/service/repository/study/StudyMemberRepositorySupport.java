package com.example.service.repository.study;

import com.example.service.entity.study.Study;
import com.example.service.entity.study.StudyMember;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepositorySupport {

    Optional<StudyMember> findMyStudyMember(Long studyId, Long memberId);

    List<Study> findAllMyStudy(Long memberId);
}
