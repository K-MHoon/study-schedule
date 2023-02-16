package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    private final MemberCommonService memberCommonService;

    @Transactional(readOnly = true)
    public Pagination<List<StudyDto>> getPublicStudyList(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAllBySecretAndIsUse(false, IsUse.Y, pageable);
        List<StudyDto> data = studyPage.getContent().stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());
        return new Pagination<>(studyPage, data);
    }

    @Transactional
    public void createPublicStudy(StudyControllerRequest.CreateStudyRequest request) {
        if(!request.getSecret() && request.getPassword() != null) {
            throw new IllegalArgumentException("private 방이 아니라면, 비밀번호를 설정할 수 없습니다.");
        }

        Study newStudy = Study.of(memberCommonService.getLoggedInMember(),
                request.getStudyName(),
                request.getFullCount(),
                request.getIsUse(),
                request.getSecret());

        studyRepository.save(newStudy);
    }
}
