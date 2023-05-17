package com.example.studyschedule.service.study;


import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.enums.exception.common.CommonErrorCode;
import com.example.studyschedule.exception.StudyScheduleException;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyRegisterService {

    private final StudyRegisterRepository studyRegisterRepository;
    private final StudyRepository studyRepository;
    private final MemberCommonService memberCommonService;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional
    public void createStudyRegister(Long studyId, StudyControllerRequest.CreateStudyRegisterRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        checkAlreadyJoinedStudy(studyId, loggedInMember);

        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyScheduleException(CommonErrorCode.NOT_FOUND));
        if (study.isFull()) {
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
        if (studyMemberRepository.existsStudyMemberByStudy_IdAndMember_Id(studyId, loggedInMember.getId())) {
            throw new IllegalArgumentException("이미 스터디에 가입되어 있습니다.");
        }
    }

    @Transactional
    public int cancelStudyRegisterAll(StudyControllerRequest.CancelStudyRegisterRequest request) {
        Member member = memberCommonService.getLoggedInMember();
        if (isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스터디 가입 요청을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        return studyRegisterRepository.updateAllCancelStudyRegister(RegisterState.CANCEL, request.getStudyRegisterList(), member.getId());
    }

    private boolean isNotSameRequestAndDataCount(StudyControllerRequest.CancelStudyRegisterRequest request, Member member) {
        return studyRegisterRepository.countAllByIdInAndRequestMember_Id(request.getStudyRegisterList(), member.getId()) != request.getStudyRegisterList().size();
    }



}
