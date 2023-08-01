package com.example.service.service.study;

import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyCode;
import com.example.common.entity.study.StudyMember;
import com.example.common.model.dto.study.StudyCodeDto;
import com.example.common.model.request.study.StudyCodeControllerRequest;
import com.example.common.model.response.study.StudyCodeControllerResponse;
import com.example.common.repository.study.StudyCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyCodeService {

    private final StudyCommonService studyCommonService;
    private final StudyCodeRepository studyCodeRepository;

    @Transactional
    public void createInviteCode(Long studyId) {
        StudyMember studyMember = studyCommonService.getMyStudyMember(studyId);
        Study study = studyMember.getStudy();
        if(!study.getSecret()) {
            throw new IllegalArgumentException("비밀 스터디만 초대 코드 생성이 가능합니다.");
        }
        StudyCode studyCode = new StudyCode(study);
        studyCodeRepository.save(studyCode);
    }


    @Transactional
    public void deleteInviteCodeAll(Long studyId, StudyCodeControllerRequest.DeleteInviteCodeAllRequest request) {
        StudyMember studyMember = studyCommonService.getMyStudyMember(studyId);

        Study study = studyMember.getStudy();
        if(!study.getSecret()) {
            throw new IllegalArgumentException("요청한 스터디는 비밀 스터디가 아닙니다.");
        }

        List<StudyCode> studyCodeList = studyCodeRepository.findAllByStudyAndIdIn(study, request.getInviteCodeList());
        if(request.getInviteCodeList().size() != studyCodeList.size()) {
            throw new IllegalArgumentException("해당 스터디에 존재하지 않는 스터디 코드가 포함되어 있습니다.");
        }

        studyCodeRepository.deleteAll(studyCodeList);
    }

    @Transactional(readOnly = true)
    public StudyCodeControllerResponse.GetStudyCodeListResponse getInviteCodeList(Long studyId) {
        StudyMember studyMember = studyCommonService.getMyStudyMember(studyId);
        List<StudyCode> studyCodeList = studyCodeRepository.findAllByStudy(studyMember.getStudy());
        List<StudyCodeDto> studyCodeDtoList = studyCodeList.stream().map(StudyCodeDto::entityToDto).collect(Collectors.toList());
        return new StudyCodeControllerResponse.GetStudyCodeListResponse(studyCodeDtoList);
    }
}
