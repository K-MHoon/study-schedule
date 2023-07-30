package com.example.service.service.study;


import com.example.service.entity.member.Member;
import com.example.service.entity.study.Study;
import com.example.service.entity.study.StudyMember;
import com.example.service.entity.study.StudyRegister;
import com.example.service.enums.RegisterState;
import com.example.service.model.dto.study.StudyCodeDto;
import com.example.service.model.dto.study.StudyDto;
import com.example.service.model.dto.study.StudyRegisterDto;
import com.example.service.model.request.study.StudyControllerRequest;
import com.example.service.model.response.study.StudyMyControllerResponse;
import com.example.service.repository.study.StudyCodeRepository;
import com.example.service.repository.study.StudyMemberRepository;
import com.example.service.repository.study.StudyRegisterRepository;
import com.example.service.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyMyService {
    private final MemberCommonService memberCommonService;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyRegisterRepository studyRegisterRepository;
    private final StudyCommonService studyCommonService;
    private final StudyCodeRepository studyCodeRepository;

    @Transactional(readOnly = true)
    public List<StudyDto> getMyStudy() {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        return loggedInMember.getStudyMemberList()
                .stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudyMyControllerResponse.GetMyStudyDetailResponse getMyStudyDetail(Long studyId) {
        StudyMember studyMember = studyCommonService.getMyStudyMember(studyId);
        StudyDto studyDto = StudyDto.entityToDtoDetail(studyMember);

        List<StudyCodeDto> studyCodeDtoList = studyCodeRepository.findAllByStudy(studyMember.getStudy())
                .stream()
                .map(StudyCodeDto::entityToDto)
                .collect(Collectors.toList());

        return new StudyMyControllerResponse.GetMyStudyDetailResponse(studyDto, studyCodeDtoList);
    }

    @Transactional(readOnly = true)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList() {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        return studyRegisterRepository.findAllByRequestMember_Id(loggedInMember.getId())
                .stream().map(StudyRegisterDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStudyState(Long studyId, Long registerId, StudyControllerRequest.UpdateStudyStateRequest request) {
        StudyMember myStudyMember = studyCommonService.getMyStudyMember(studyId);
        StudyRegister studyRegister = studyRegisterRepository.findByIdAndRequestStudy_Id(registerId, studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 스터디에 가입 요청이 존재하지 않습니다. study Id = " + studyId + " register Id = " + registerId));

        RegisterState asIsState = studyRegister.getState();
        studyRegister.updateRegisterState(RegisterState.convertStringToRegisterState(request.getState()));
        RegisterState tobeState = studyRegister.getState();

        if (asIsState == RegisterState.READ && asIsState != tobeState) {
            if (tobeState == RegisterState.PASS) {
                studyMemberRepository.save(new StudyMember(studyRegister.getRequestMember(), myStudyMember.getStudy()));
            }
            studyRegister.updateApproval(myStudyMember.getMember());
        }
    }

    @Transactional
    public void kickOutStudyMember(Long studyId, Long memberId) {
        StudyMember myStudyMember = studyCommonService.getMyStudyMember(studyId);
        if (myStudyMember.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("자신은 강퇴할 수 없습니다.");
        }
        StudyMember studyMember = studyMemberRepository.findByStudy_IdAndMember_Id(studyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 스터디 회원이 존재하지 않습니다. studyId = " + studyId + " memberId = " + memberId));
        studyMemberRepository.delete(studyMember);
    }

    @Transactional
    public void updateMyStudy(Long studyId, StudyControllerRequest.UpdateStudyRequest request) {
        StudyMember myStudyMember = studyCommonService.getMyStudyMember(studyId);
        Study study = myStudyMember.getStudy();

        study.updateName(request.getStudyName());
        study.updateContent(request.getContent());
        study.updateFullCount(request.getFullCount());
    }
}
