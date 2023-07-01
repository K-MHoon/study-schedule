package com.example.studyschedule.service.study;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.enums.exception.common.CommonErrorCode;
import com.example.studyschedule.exception.StudyScheduleException;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class StudyRegisterServiceTest extends TestHelper {

    @Autowired
    StudyRegisterRepository studyRegisterRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyRegisterService service;

    @Test
    @DisplayName("스터디 가입 요청에 성공한다.")
    void successCreateStudyRegister() {
        Study study = studyHelper.createSimpleStudy(member);
        StudyControllerRequest.CreateStudyRegisterRequest request = new StudyControllerRequest.CreateStudyRegisterRequest("목표 테스트", "목적 테스트", "주석 테스트");

        service.createStudyRegister(study.getId(), request);

        List<StudyRegister> result = studyRegisterRepository.findAll();
        assertAll(() -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getGoal()).isEqualTo("목표 테스트"),
                () -> assertThat(result.get(0).getObjective()).isEqualTo("목적 테스트"),
                () -> assertThat(result.get(0).getComment()).isEqualTo("주석 테스트"),
                () -> assertThat(result.get(0).getState()).isEqualTo(RegisterState.NO_READ),
                () -> assertThat(result.get(0).getRequestMember()).isEqualTo(member),
                () -> assertThat(result.get(0).getRequestStudy()).isEqualTo(study)
        );
    }

    @Test
    @DisplayName("이미 가입된 스터디의 경우 예외가 발생한다.")
    void rejectWhenRequestAlreadyJoinedStudy() {
        Study study = studyHelper.createSimpleStudy(member);
        StudyMember savedStudyMember = studyMemberRepository.save(new StudyMember(member, study));
        study.getStudyMemberList().add(savedStudyMember);
        Study savedStudy = studyRepository.save(study);

        StudyControllerRequest.CreateStudyRegisterRequest request = new StudyControllerRequest.CreateStudyRegisterRequest("목표 테스트", "목적 테스트", "주석 테스트");

        assertThatThrownBy(() -> service.createStudyRegister(savedStudy.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 스터디에 가입되어 있습니다.");
    }

    @Test
    @DisplayName("요청한 스터디가 존재하지 않을 경우, 예외가 발생한다.")
    void rejectWhenNotFoundStudy() {
        StudyControllerRequest.CreateStudyRegisterRequest request = new StudyControllerRequest.CreateStudyRegisterRequest("목표 테스트", "목적 테스트", "주석 테스트");

        assertThatThrownBy(() -> service.createStudyRegister(Long.MAX_VALUE, request))
                .isInstanceOf(StudyScheduleException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("스터디 정원이 가득 찬 경우 예외가 발생한다.")
    void rejectWhenStudyIsFull() {
        Study study = Study.ofPublic(member, "스터디 테스트", "스터디 설명", 1L, IsUse.Y);
        StudyMember savedStudyMember = studyMemberRepository.save(new StudyMember(memberHelper.getUnknownMember(), study));
        study.getStudyMemberList().add(savedStudyMember);
        Study savedStudy = studyRepository.save(study);

        StudyControllerRequest.CreateStudyRegisterRequest request = new StudyControllerRequest.CreateStudyRegisterRequest("목표 테스트", "목적 테스트", "주석 테스트");

        assertThatThrownBy(() -> service.createStudyRegister(savedStudy.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 정원이 가득 찼습니다.");
    }

    @Test
    @DisplayName("스터디 가입 요청을 취소로 변경한다.")
    void cancelStudyRegisterAll() {
        // given
        Study savedStudy = studyHelper.createStudyWithStudyMember(member);
        List<Member> memberList = Arrays.asList(member);
        List<Long> studyRegisterIdList = studyHelper
                .createStudyRegister(savedStudy, memberList, RegisterState.READ)
                .stream()
                .map(studyRegister -> studyRegister.getId())
                .collect(Collectors.toList());

        StudyControllerRequest.CancelStudyRegisterRequest request = new StudyControllerRequest.CancelStudyRegisterRequest(studyRegisterIdList);

        // when
        service.cancelStudyRegisterAll(request);

        // then
        entityManagerFlushAndClear();
        List<StudyRegister> result = studyRegisterRepository.findAll();
        assertThat(result).hasSize(studyRegisterIdList.size());
        assertThat(result).extracting("state").containsExactlyInAnyOrder(RegisterState.CANCEL);
    }

    @Test
    @DisplayName("다른 사용자의 스터디 가입 요청을 취소 요청하는 경우 예외가 발생한다.")
    void rejectWhenStudyRegisterCancel() {
        // given
        Study savedStudy = studyHelper.createStudyWithStudyMember(member);
        StudyRegister studyRegister = studyHelper.createStudyRegister(savedStudy, memberHelper.getUnknownMember(), RegisterState.READ);

        StudyControllerRequest.CancelStudyRegisterRequest request = new StudyControllerRequest.CancelStudyRegisterRequest(Arrays.asList(studyRegister.getId()));

        // when & then
        assertThatThrownBy(() -> service.cancelStudyRegisterAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 스터디 가입 요청을 포함하고 있습니다. memberId = " + member.getMemberId());
    }
}