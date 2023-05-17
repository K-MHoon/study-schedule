package com.example.studyschedule.service.study;


import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.dto.study.StudyRegisterDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.service.member.MemberCommonService;
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

    @Transactional(readOnly = true)
    public List<StudyDto> getMyStudy() {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        return loggedInMember.getStudyMemberList()
                .stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudyDto getMyStudyDetail(Long studyId) {
        StudyMember studyMember = getMyStudyMember(studyId);
        return StudyDto.entityToDtoDetail(studyMember);
    }

    @Transactional(readOnly = true)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList() {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        return studyRegisterRepository.findAllByRequestMember_Id(loggedInMember.getId())
                .stream().map(StudyRegisterDto::entityToDto)
                .collect(Collectors.toList());
    }

    private StudyMember getMyStudyMember(Long studyId) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        StudyMember studyMember = studyMemberRepository.findMyStudyMember(studyId, loggedInMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않거나 가입되지 않은 스터디 입니다."));
        if (!studyMember.getStudy().getLeader().equals(loggedInMember)) {
            throw new IllegalArgumentException("본인의 스터디가 아닙니다.");
        }
        return studyMember;
    }

    @Transactional
    public void updateStudyState(Long studyId, Long registerId, StudyControllerRequest.UpdateStudyStateRequest request) {
        StudyMember myStudyMember = getMyStudyMember(studyId);
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
        StudyMember myStudyMember = getMyStudyMember(studyId);
        if (myStudyMember.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("자신은 강퇴할 수 없습니다.");
        }
        StudyMember studyMember = studyMemberRepository.findByStudy_IdAndMember_Id(studyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 스터디 회원이 존재하지 않습니다. studyId = " + studyId + " memberId = " + memberId));
        studyMemberRepository.delete(studyMember);
    }

}
