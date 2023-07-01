package com.example.studyschedule.service.study;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StudyMyServiceTest extends TestHelper {

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyRegisterRepository studyRegisterRepository;

    @Autowired
    StudyMyService service;

    @Test
    @DisplayName("로그인한 계정과 연관된 스터디 정보만 가지고 온다.")
    void successGetMyStudyList() {
        Study study1 = Study.ofPublic(member, "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        studyMemberRepository.save(new StudyMember(member, study1));
        Study study2 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트2", "스터디 설명", 11L, IsUse.Y);
        studyMemberRepository.save(new StudyMember(member, study2));
        Study study3 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트3", "스터디 설명", 12L, IsUse.Y);
        studyMemberRepository.save(new StudyMember(member, study3));
        studyRepository.saveAll(Arrays.asList(study1, study2, study3));
        entityManagerFlushAndClear();

        List<StudyDto> result = service.getMyStudy();

        assertThat(result).hasSize(3);
        assertThat(result).extracting("id").containsExactlyInAnyOrder(study1.getId(), study2.getId(), study3.getId());
        assertThat(result).filteredOn(StudyDto::getIsMine).hasSize(1);
        assertThat(result).filteredOn(StudyDto::getIsMine).singleElement().extracting("id").isEqualTo(study1.getId());
    }

    @Test
    @DisplayName("내 스터디 세부 정보를 가지고 온다.")
    void successGetMeyStudyDetail() {
        // given
        Study study = Study.ofPublic(member, "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        studyMemberRepository.save(new StudyMember(member, study));
        Study savedStudy = studyRepository.save(study);

        List<Member> memberList = memberHelper.createTestMembersAndSaveByCount(5);
        studyHelper.createStudyMember(savedStudy, memberList);

        List<Member> studyRegisterMemberList = memberHelper.createTestMembersAndSaveByCount(5, 9);
        studyHelper.createStudyRegister(savedStudy, studyRegisterMemberList);

        entityManagerFlushAndClear();

        // when
        StudyDto result = service.getMyStudyDetail(savedStudy.getId());
        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getStudyName()).isEqualTo("스터디 테스트1");
        assertThat(result.getRegisteredMemberList()).hasSize(6);
        assertThat(result.getRegisterRequestList()).hasSize(4);
    }

    @Test
    @DisplayName("접근할 수 없는 스터디라면 예외가 발생한다.")
    void rejectWhenNotAccessStudy() {
        // given
        Study study = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study savedStudy = studyRepository.save(study);

        // when & then
        assertThatThrownBy(() -> service.getMyStudyDetail(savedStudy.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디가 존재하지 않거나 가입되지 않은 스터디 입니다.");
    }

    @Test
    @DisplayName("본인이 방장이 아닌 스터디에 접근하면 예외가 발생한다.")
    void rejectWhenNotLeader() {
        // given
        Study savedStudy = studyHelper.createStudyWithStudyMember(memberHelper.getUnknownMember());
        StudyMember studyMember = new StudyMember(member, savedStudy);
        studyMemberRepository.save(studyMember);

        // when & then
        assertThatThrownBy(() -> service.getMyStudyDetail(savedStudy.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 스터디가 아닙니다.");
    }

    @ParameterizedTest
    @MethodSource("changeUpdateState")
    @DisplayName("스터디 상태를 업데이트 한다.")
    void updateStudyState(String state, RegisterState registerState) {
        Study savedStudy = studyHelper.createStudyWithStudyMember(member);
        List<Member> memberList = Arrays.asList(memberHelper.getUnknownMember());
        List<StudyRegister> studyRegister = studyHelper.createStudyRegister(savedStudy, memberList);

        service.updateStudyState(savedStudy.getId(), studyRegister.get(0).getId(), new StudyControllerRequest.UpdateStudyStateRequest(state));

        List<StudyRegister> result = studyRegisterRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo(registerState);
    }

    @Test
    @DisplayName("스터디 가입 상태가 Pass가 되면 스터디 가입이 완료된다.")
    void addStudyMemberWhenUpdateStudyStatePass() {
        // given
        Study savedStudy = studyHelper.createStudyWithStudyMember(member);
        List<Member> memberList = Arrays.asList(memberHelper.getUnknownMember());
        List<StudyRegister> studyRegister = studyHelper.createStudyRegister(savedStudy, memberList, RegisterState.READ);

        // when
        service.updateStudyState(savedStudy.getId(), studyRegister.get(0).getId(), new StudyControllerRequest.UpdateStudyStateRequest("pass"));

        // then
        assertStudyRegister(RegisterState.PASS);
        assertHasStudyMember(savedStudy, memberList);
    }

    @Test
    @DisplayName("스터디 가입 상태가 Reject되면 스터디 가입이 되지 않는다.")
    void addStudyMemberWhenUpdateStudyStateReject() {
        // given
        Study savedStudy = studyHelper.createStudyWithStudyMember(member);
        List<Member> memberList = Arrays.asList(memberHelper.getUnknownMember());
        List<StudyRegister> studyRegister = studyHelper.createStudyRegister(savedStudy, memberList, RegisterState.READ);

        // when
        service.updateStudyState(savedStudy.getId(), studyRegister.get(0).getId(), new StudyControllerRequest.UpdateStudyStateRequest("reject"));

        // then
        assertStudyRegister(RegisterState.REJECT);
        assertStudyMemberIsEmpty(savedStudy, memberList);
    }

    private void assertStudyMemberIsEmpty(Study savedStudy, List<Member> memberList) {
        Optional<StudyMember> registeredStudyMember = studyMemberRepository.findByStudy_IdAndMember_Id(savedStudy.getId(), memberList.get(0).getId());
        assertThat(registeredStudyMember).isEmpty();
    }

    private void assertHasStudyMember(Study savedStudy, List<Member> memberList) {
        Optional<StudyMember> registeredStudyMember = studyMemberRepository.findByStudy_IdAndMember_Id(savedStudy.getId(), memberList.get(0).getId());
        assertThat(registeredStudyMember).isNotNull();
    }

    private void assertStudyRegister(RegisterState pass) {
        List<StudyRegister> result = studyRegisterRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo(pass);
        assertThat(result.get(0).getApprovalBy()).isEqualTo(member);
    }

    @Test
    @DisplayName("스터디 강퇴에 성공한다.")
    void kickOffStudyMember() {
        // given
        Member simpleMember = memberHelper.createSimpleMember("kickOffTest");
        Study study = Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y);
        Study savedStudy = studyRepository.save(study);
        studyMemberRepository.save(new StudyMember(member, savedStudy));
        studyMemberRepository.save(new StudyMember(simpleMember, savedStudy));

        // when
        service.kickOutStudyMember(savedStudy.getId(), simpleMember.getId());
        entityManagerFlushAndClear();

        // then
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList.get(0).getStudyMemberList()).hasSize(1);
        assertThat(studyList.get(0).getStudyMemberList().get(0).getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("본인 강퇴 요청은 거절한다.")
    void rejectKickOffStudyMemberSelf() {
        // given
        Member simpleMember = memberHelper.createSimpleMember("kickOffTest");
        Study study = Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y);
        Study savedStudy = studyRepository.save(study);
        studyMemberRepository.save(new StudyMember(member, savedStudy));
        studyMemberRepository.save(new StudyMember(simpleMember, savedStudy));

        // when
        assertThatThrownBy(() -> service.kickOutStudyMember(savedStudy.getId(), member.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자신은 강퇴할 수 없습니다.");

    }

    @Test
    @DisplayName("스터디에 가입되지 않은 회원 강퇴를 요청할 경우 예외가 발생한다.")
    void rejectKickOffOtherStudyMember() {
        // given
        Member simpleMember = memberHelper.createSimpleMember("kickOffTest");
        Member otherMember = memberHelper.createSimpleMember("otherMember");
        Study study = Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y);
        Study savedStudy = studyRepository.save(study);
        studyMemberRepository.save(new StudyMember(member, savedStudy));
        studyMemberRepository.save(new StudyMember(simpleMember, savedStudy));

        // when
        assertThatThrownBy(() -> service.kickOutStudyMember(savedStudy.getId(), otherMember.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당하는 스터디 회원이 존재하지 않습니다. studyId = " + savedStudy.getId() + " memberId = " + otherMember.getId());
    }

    static Stream<Arguments> changeUpdateState() {
        return Stream.of(
                Arguments.arguments("read", RegisterState.READ),
                Arguments.arguments("pass", RegisterState.PASS),
                Arguments.arguments("reject", RegisterState.REJECT));
    }
}