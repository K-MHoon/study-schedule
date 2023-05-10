package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.enums.exception.common.CommonErrorCode;
import com.example.studyschedule.exception.StudyScheduleException;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.dto.study.StudyRegisterDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    private final StudyMemberRepository studyMemberRepository;

    private final StudyRegisterRepository studyRegisterRepository;

    @Transactional(readOnly = true)
    public Pagination<List<StudyDto>> getPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request) {
        Page<Study> studyPage = studyRepository.findAllPublicStudyList(request);

        List<StudyDto> data = studyPage.getContent().stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());

        return new Pagination<>(studyPage, data);
    }

    @Transactional
    public void createPublicStudy(StudyControllerRequest.CreateStudyRequest request) {
        Study newStudy = Study.ofPublic(memberCommonService.getLoggedInMember(),
                request.getStudyName(),
                request.getContent(),
                request.getFullCount(),
                request.getIsUse());

        if(request.getSecret()) {
            newStudy.changeToPrivate(passwordEncoder.encode(request.getPassword()));
        }

        Study savedStudy = studyRepository.save(newStudy);
        studyMemberRepository.save(new StudyMember(memberCommonService.getLoggedInMember(), savedStudy));
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Member member = memberCommonService.getLoggedInMember();

        Study study = studyRepository.findByIdAndLeaderAndIsUse(studyId, member, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("해당 Study는 삭제할 수 없습니다. id = " + studyId));

        studyRepository.delete(study);
    }

    @Transactional(readOnly = true)
    public StudyDto getPublicStudyDetail(Long studyId) {
        Study publicStudy = studyRepository.findByIdAndSecretAndIsUse(studyId, false, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디이거나 비공개 스터디 입니다. id = " + studyId));

        return StudyDto.entityToDto(publicStudy);
    }

    @Transactional
    public void deleteStudyMemberAll(StudyControllerRequest.DeleteStudyMemberAllRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<StudyMember> studyMemberList = getRequestStudyMemberList(request, loggedInMember);

        if(sizeNotEqual(request, studyMemberList)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스터디를 포함하고 있습니다. memberId = " + loggedInMember.getMemberId());
        }

        studyMemberRepository.deleteAllInBatch(studyMemberList);
    }

    private boolean sizeNotEqual(StudyControllerRequest.DeleteStudyMemberAllRequest request, List<StudyMember> studyMemberList) {
        return studyMemberList.size() != request.getStudyList().size();
    }

    private List<StudyMember> getRequestStudyMemberList(StudyControllerRequest.DeleteStudyMemberAllRequest request, Member loggedInMember) {
        return studyMemberRepository.findAllByStudy_IdInAndMember_Id(request.getStudyList(), loggedInMember.getId());
    }

    @Transactional
    public void createStudyRegister(Long studyId, StudyControllerRequest.CreateStudyRegisterRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        checkAlreadyJoinedStudy(studyId, loggedInMember);

        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyScheduleException(CommonErrorCode.NOT_FOUND));
        if(study.isFull()) {
            throw new IllegalArgumentException("스터디 정원이 가득 찼습니다.");
        }

        StudyRegister register = StudyRegister.builder()
                .goal(request.getGoal())
                .objective(request.getObjective())
                .comment(request.getComment())
                .state(RegisterState.NO_READ)
                .requestStudy(study)
                .requestMember(loggedInMember)
                .build();

        studyRegisterRepository.save(register);
    }

    private void checkAlreadyJoinedStudy(Long studyId, Member loggedInMember) {
        if(studyMemberRepository.existsStudyMemberByStudy_IdAndMember_Id(studyId, loggedInMember.getId())) {
            throw new IllegalArgumentException("이미 스터디에 가입되어 있습니다.");
        }
    }

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

    private StudyMember getMyStudyMember(Long studyId) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        StudyMember studyMember = studyMemberRepository.findMyStudyMember(studyId, loggedInMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않거나 가입되지 않은 스터디 입니다."));
        if(!studyMember.getStudy().getLeader().equals(loggedInMember)) {
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

        if(asIsState == RegisterState.READ && asIsState != tobeState) {
            if(tobeState == RegisterState.PASS) {
                studyMemberRepository.save(new StudyMember(studyRegister.getRequestMember(), myStudyMember.getStudy()));
            }
            studyRegister.updateApproval(myStudyMember.getMember());
        }
    }

    @Transactional
    public void kickOutStudyMember(Long studyId, Long memberId) {
        StudyMember myStudyMember = getMyStudyMember(studyId);
        if(myStudyMember.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("자신은 강퇴할 수 없습니다.");
        }
        StudyMember studyMember = studyMemberRepository.findByStudy_IdAndMember_Id(studyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 스터디 회원이 존재하지 않습니다. studyId = " + studyId + " memberId = " + memberId));
        studyMemberRepository.delete(studyMember);
    }

    @Transactional(readOnly = true)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList() {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        return studyRegisterRepository.findAllByRequestMember_Id(loggedInMember.getId())
                .stream().map(StudyRegisterDto::entityToDto)
                .collect(Collectors.toList());
    }
}

