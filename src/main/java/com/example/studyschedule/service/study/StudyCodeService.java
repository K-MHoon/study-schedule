package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyCode;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.model.request.study.StudyCodeControllerRequest;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
