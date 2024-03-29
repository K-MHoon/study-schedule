package com.example.service.service.study;

import com.example.service.TestHelper;
import com.example.common.entity.member.Member;
import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyCode;
import com.example.common.entity.study.StudyMember;
import com.example.common.enums.IsUse;
import com.example.common.model.dto.Pagination;
import com.example.common.model.dto.study.StudyDto;
import com.example.service.controller.request.study.StudyControllerRequest;
import com.example.common.repository.study.StudyCodeRepository;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.common.repository.study.StudyRegisterRepository;
import com.example.common.repository.study.StudyRepository;
import com.example.service.service.study.request.StudyServiceRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudyServiceTest extends TestHelper {

    @Autowired
    StudyService service;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    StudyRegisterRepository studyRegisterRepository;

    @Autowired
    StudyCodeRepository studyCodeRepository;
    
    @Test
    @DisplayName("현재 사용 가능하고 공개된 스터디가 조회된다.")
    void getPublicStudyListTest() {
        // given
        studyRepository.save(Study.ofPublic(member, "Study Test", "스터디", 10L, IsUse.Y));
        Pageable pageRequest = PageRequest.of(0, 10);

        // when
        Pagination<List<StudyDto>> response = service.getPublicStudyList(null, null, pageRequest);

        // then
        assertAll(() -> assertThat(response.getData()).hasSize(1),
                () -> assertThat(response.getData().get(0).getStudyName()).isEqualTo("Study Test"),
                () -> assertThat(response.getData().get(0).getLeaderId()).isEqualTo(member.getMemberId()));
    }

    @Test
    @DisplayName("공개 스터디를 정상 생성한다.")
    void createPublicStudy() {
        // given
        StudyServiceRequest.CreateStudy request
                = StudyServiceRequest.CreateStudy.builder()
                .studyName("스터디 테스트")
                .content("공부 스터디 입니다.")
                .secret(false)
                .password(null)
                .fullCount(10L)
                .isUse(IsUse.Y)
                .build();

        // when
        service.createPublicStudy(request);

        // then
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(1)
                .extracting( "name", "content", "secret", "password", "fullCount", "isUse")
                .contains(tuple("스터디 테스트", "공부 스터디 입니다.", false, null, 10L, IsUse.Y));
    }

    @Test
    @DisplayName("공개 스터디 생성에 성공하면 스터디 장이므로 자동으로 가입된다.")
    void joinNewStudyWhenCreatePublicStudySuccess() {
        // given
        StudyServiceRequest.CreateStudy request
                = StudyServiceRequest.CreateStudy.builder()
                .studyName("스터디 테스트")
                .content("공부 스터디 입니다.")
                .secret(false)
                .password(null)
                .fullCount(10L)
                .isUse(IsUse.Y)
                .build();

        // when
        service.createPublicStudy(request);
        entityManagerFlushAndClear();

        // then
        List<Study> studyList = studyRepository.findAll();
        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getLeader().getMemberId()).isEqualTo("testMember"),
                () -> assertThat(studyList.get(0).getStudyMemberList()).hasSize(1),
                () -> assertThat(studyList.get(0).getStudyMemberList().get(0).getMember().getMemberId()).isEqualTo("testMember")
        );
    }

    @Test
    @DisplayName("단일 공개 스터디를 정상 조회한다.")
    void getPublicStudyDetail() {
        // given
        Study study = studyHelper.createSimpleStudy(member);
        study.addStudyMember(memberHelper.getUnknownMember());
        study.addStudyMember(memberHelper.getUnknownMember());

        // when
        StudyDto result = service.getPublicStudyDetail(study.getId(), null);

        // then
        assertThat(result.getId()).isEqualTo(study.getId());
    }

    @Test
    @DisplayName("비공개 스터디는 공개 스터디로 조회되지 않는다.")
    @Disabled("공개 스터디 조건 수정으로 더 이상 사용하지 않음")
    void doNotFindStudyIfStudyIsSecret() {
        // given
        Study study = studyHelper.createSimpleStudy(member);
        study.changeToPrivate("test");
        study.addStudyMember(memberHelper.getUnknownMember());
        study.addStudyMember(memberHelper.getUnknownMember());

        // when & then
        assertThrows(EntityNotFoundException.class
                , () -> service.getPublicStudyDetail(study.getId(), "inviteCode"));
    }

    @Test
    @DisplayName("비공개 스터디를 정상 생성한다.")
    void createPrivateStudy() {
        // given
        StudyServiceRequest.CreateStudy request
                = StudyServiceRequest.CreateStudy.builder()
                .studyName("스터디 테스트")
                .content("공부 스터디 입니다.")
                .secret(true)
                .password("test")
                .fullCount(10L)
                .isUse(IsUse.Y)
                .build();

        // when
        service.createPublicStudy(request);

        // then
        List<Study> studyList = studyRepository.findAll();
        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getName()).isEqualTo("스터디 테스트"),
                () -> assertThat(studyList.get(0).getSecret()).isTrue(),
                () -> assertThat(passwordEncoder.matches("test", studyList.get(0).getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 삭제에 성공한다.")
    void deleteStudy() {
        Study study = studyHelper.createSimpleStudy(member);
        study.addStudyMember(memberHelper.getUnknownMember());
        study.addStudyMember(memberHelper.getUnknownMember());

        service.deleteStudy(study.getId());

        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).isEmpty();
    }

    @Test
    @DisplayName("스터디가 삭제될 때, 관련된 StudyMember가 모두 삭제 된다.")
    void deleteAllLinkedStudyMembersWhenDeleteStudy() {
        Study study = studyHelper.createSimpleStudy(member);
        study.addStudyMember(memberHelper.getUnknownMember());
        study.addStudyMember(memberHelper.getUnknownMember());

        service.deleteStudy(study.getId());

        List<StudyMember> studyMemberList = studyMemberRepository.findAll();
        assertThat(studyMemberList).isEmpty();
    }

    @Test
    @DisplayName("여러 스터디를 탈퇴하면, StudyMember 관계가 모두 삭제된다.")
    void deleteStudyMemberAllWhenStudyOut() {
        // given
        Study study1 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트2", "스터디 설명", 10L, IsUse.Y);
        studyRepository.save(study1);
        studyRepository.save(study2);
        studyMemberRepository.save(new StudyMember(member, study1));
        studyMemberRepository.save(new StudyMember(member, study2));
        StudyServiceRequest.DeleteStudyMemberAll request = StudyServiceRequest.DeleteStudyMemberAll.builder()
                .studyList(Arrays.asList(study1.getId(), study2.getId()))
                .build();

        // when
        service.deleteStudyMemberAll(request);

        // then
        List<StudyMember> studyMemberList = studyMemberRepository.findAll();
        assertThat(studyMemberList).isEmpty();
    }

    @Test
    @DisplayName("가입되지 않은 스터디 탈퇴를 요청하는 경우, 예외가 발생한다.")
    void causeExceptionWhenNotJoinedStudyOutRequest() {
        // given
        Study study1 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(memberHelper.getUnknownMember(), "스터디 테스트2", "스터디 설명", 10L, IsUse.Y);
        studyRepository.save(study1);
        studyRepository.save(study2);
        studyMemberRepository.save(new StudyMember(member, study1));
        StudyServiceRequest.DeleteStudyMemberAll request = StudyServiceRequest.DeleteStudyMemberAll.builder()
                .studyList(Arrays.asList(study1.getId(), study2.getId()))
                .build();
        // when & then
        assertThatThrownBy(() -> service.deleteStudyMemberAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 스터디를 포함하고 있습니다. memberId = " + member.getMemberId());
    }



    @Test
    @WithMockUser(username = "useMember")
    @DisplayName("비밀 스터디를 찾는다.")
    void findSecretStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyCode studyCode = studyCodeRepository.save(new StudyCode(study));
        Member newMember = memberHelper.createSimpleMember("useMember");

        // when

        Long secretStudyId = service.findSecretStudy(studyCode.getInviteCode());

        // then
        assertThat(secretStudyId).isEqualTo(study.getId());
        assertThat(studyCode.getUseMember().getId()).isEqualTo(newMember.getId());
    }

    @Test
    @WithMockUser(username = "useMember")
    @DisplayName("해당 초대 코드가 올바르지 않은 경우 예외가 발생한다.")
    void causeExceptionWhenInviteCodInvalid() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyCode studyCode = studyCodeRepository.save(new StudyCode(study));
        Member newMember = memberHelper.createSimpleMember("useMember");

        // when & then
        assertThatThrownBy(() -> service.findSecretStudy(UUID.randomUUID().toString()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("코드에 해당하는 스터디가 존재하지 않습니다.");
    }

    @Test
    @WithMockUser(username = "useMember")
    @DisplayName("이미 사용된 코드인 경우 예외가 발생한다.")
    void causeExceptionWhenInviteCodAlreadyUsed() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyCode studyCode = studyCodeRepository.save(new StudyCode(study));
        Member newMember = memberHelper.createSimpleMember("useMember");
        studyCode.updateUseMember(newMember);

        // when & then
        assertThatThrownBy(() -> service.findSecretStudy(UUID.randomUUID().toString()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("코드에 해당하는 스터디가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("공개 스터디를 비밀 스터디로 변경한다.")
    void changeStudyToSecretStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(true)
                .password("test1234")
                .build();

        // when
        service.changeStudySecretOrPublic(study.getId(), request);

        // then
        assertThat(study.getSecret()).isTrue();
        assertThat(study.getPassword()).isEqualTo(request.getPassword());
    }

    @Test
    @DisplayName("비밀 스터디 비밀번호를 변경한다.")
    void changeStudyPassword() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(true)
                .password("test5678")
                .build();

        // when
        service.changeStudySecretOrPublic(study.getId(), request);

        // then
        assertThat(study.getPassword()).isEqualTo(request.getPassword());
    }

    @Test
    @DisplayName("비밀 스터디를 공개 스터디로 변경한다.")
    void changeStudyToPublicStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(false)
                .password("test1234")
                .build();

        // when
        service.changeStudySecretOrPublic(study.getId(), request);

        // then
        assertThat(study.getSecret()).isFalse();
        assertThat(study.getPassword()).isNull();
    }

    @Test
    @DisplayName("공개 전환 비밀번호가 틀리는 경우 예외가 발생한다.")
    void causeExceptionWhenInputInvalidPassword() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(false)
                .password("test1235")
                .build();

        // when & then
        assertThatThrownBy(() -> service.changeStudySecretOrPublic(study.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 전환 비밀번호가 틀렸습니다.");
    }

    @Test
    @DisplayName("비밀 스터디가 아닌데 공개 전환 요청하는 경우 예외가 발생한다.")
    void causeExceptionWhenChangePublicStudyIfNotSecretStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(false)
                .password("test1235")
                .build();

        // when & then
        assertThatThrownBy(() -> service.changeStudySecretOrPublic(study.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디는 비밀 스터디가 아닙니다.");
    }

    @ParameterizedTest
    @DisplayName("비밀 스터디로 전환하는데 비밀번호가 없는 경우 예외가 발생한다.")
    @NullAndEmptySource
    void causeExceptionWhenChangePrivateStudyIfNotContainPassword(String empty) {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        StudyServiceRequest.ChangeSecret request = StudyServiceRequest.ChangeSecret.builder()
                .secret(true)
                .password(empty)
                .build();

        // when & then
        assertThatThrownBy(() -> service.changeStudySecretOrPublic(study.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀 스터디에는 반드시 비밀번호가 포함되어야 합니다.");
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 되지 않은 사용자가 비밀 스터디에 접근하려고 할 때 예외가 발생한다.")
    void causeExceptionWhenNotLoggedInUserAccessStudyDetail() {
        Study simpleStudy = studyHelper.createSimpleStudy(member);

        assertThatThrownBy(() -> service.getPublicStudyDetail(simpleStudy.getId(), "inviteCode"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("member ID에 해당하는 멤버를 찾을 수 없습니다. memberId = anonymous");
    }

    @Test
    @DisplayName("잘못된 스터디 코드로 비밀 스터디에 접근하려고 할 때 예외가 발생한다.")
    void causeExceptionWhenAccessStudyDetailIfHasInvalidInviteCode() {
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        simpleStudy.changeToPrivate("password");

        assertThatThrownBy(() -> service.getPublicStudyDetail(simpleStudy.getId(), "inviteCode"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 초대 코드 입니다.");
    }

    @Test
    @DisplayName("로그인한 사용자의 스터디 코드가 아닌 경우 예외가 발생한다.")
    void causeExceptionWhenAccessStudyDetailIfHasOtherInviteCode() {
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        simpleStudy.changeToPrivate("password");

        StudyCode studyCode = new StudyCode(simpleStudy);
        studyCode.updateUseMember(memberHelper.createSimpleMember("otherMember"));
        studyCodeRepository.save(studyCode);
        entityManagerFlushAndClear();

        assertThatThrownBy(() -> service.getPublicStudyDetail(simpleStudy.getId(), studyCode.getInviteCode()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 초대 코드 입니다.");
    }

    @Test
    @DisplayName("발급 받은 스터디 코드로 조회하는 경우 스터디 상세 정보를 조회할 수 있다.")
    void getSecretStudyDetailWhenHasInviteCodeByLoggedInUser() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        simpleStudy.changeToPrivate("password");

        StudyCode studyCode = studyCodeHelper.createStudyCode(member, simpleStudy);
        entityManagerFlushAndClear();

        // when
        StudyDto result = service.getPublicStudyDetail(simpleStudy.getId(), studyCode.getInviteCode());

        // then
        assertThat(result.getId()).isEqualTo(simpleStudy.getId());
    }
}