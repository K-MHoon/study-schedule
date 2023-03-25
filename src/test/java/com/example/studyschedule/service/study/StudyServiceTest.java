package com.example.studyschedule.service.study;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithMockUser(username = "testMember", authorities = {"USER"})
class StudyServiceTest extends TestHelper {

    @Autowired
    StudyService service;

    private Member member;

    @BeforeEach
    void init() {
        this.member = createSimpleMember();
    }

    @Test
    @DisplayName("현재 사용 가능하고 공개된 스터디가 조회된다.")
    void getPublicStudyListTest() {
        studyRepository.save(Study.ofPublic(member, "Study Test", "스터디", 10L, IsUse.Y));
        Pageable pageRequest = PageRequest.of(0, 10);

        Pagination<List<StudyDto>> response = service.getPublicStudyList(pageRequest);

        assertAll(() -> assertThat(response.getData()).hasSize(1),
                () -> assertThat(response.getData().get(0).getStudyName()).isEqualTo("Study Test"),
                () -> assertThat(response.getData().get(0).getLeaderName()).isEqualTo(member.getName()));
    }

    @Test
    @DisplayName("공개 스터디를 정상 생성한다.")
    void createPublicStudy() {
        // given
        String studyName = "스터디 테스트";
        String content = "공부 스터디 입니다.";
        Boolean secret = false;
        String password = null;
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;
        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, content, secret, password, fullCount, isUse);

        // when
        service.createPublicStudy(request);

        // then
        List<Study> studyList = studyRepository.findAll();
        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getName()).isEqualTo(studyName)
        );
    }

    @Test
    @DisplayName("공개 스터디 생성에 성공하면 스터디 장이므로 자동으로 가입된다.")
    void joinNewStudyWhenCreatePublicStudySuccess() {
        // given
        String studyName = "스터디 테스트";
        String content = "공부 스터디 입니다.";
        Boolean secret = false;
        String password = "";
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;
        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, content, secret, password, fullCount, isUse);

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
        Study study = getStudyFixture();
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        // when
        StudyDto studyDetail = service.getPublicStudyDetail(mockStudy.getId());

        // then
        assertThat(studyDetail.getId()).isEqualTo(mockStudy.getId());
    }

    private Study getStudyFixture() {
        return Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y);
    }

    @Test
    @DisplayName("비공개 스터디는 공개 스터디로 조회되지 않는다.")
    void doNotFindStudyIfStudyIsSecret() {
        // given
        Study study = getStudyFixture();
        study.changeToPrivate("test");
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        // when & then
        assertThrows(EntityNotFoundException.class
                , () -> service.getPublicStudyDetail(mockStudy.getId()));
    }

    @Test
    @DisplayName("비공개 스터디를 정상 생성한다.")
    void createPrivateStudy() {
        // given
        String studyName = "스터디 테스트";
        String content = "공부 스터디 입니다.";
        Boolean secret = true;
        String password = "test";
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;
        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, content,  secret, password, fullCount, isUse);

        // when
        service.createPublicStudy(request);

        // then
        List<Study> studyList = studyRepository.findAll();
        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getName()).isEqualTo(studyName),
                () -> assertThat(studyList.get(0).getSecret()).isTrue(),
                () -> assertThat(passwordEncoder.matches(password, studyList.get(0).getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 삭제에 성공한다.")
    void deleteStudy() {
        Study study = getStudyFixture();
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        service.deleteStudy(mockStudy.getId());

        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).isEmpty();
    }

    @Test
    @DisplayName("스터디가 삭제될 때, 관련된 StudyMember가 모두 삭제 된다.")
    void deleteAllLinkedStudyMembersWhenDeleteStudy() {
        Study study = getStudyFixture();
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        service.deleteStudy(mockStudy.getId());

        List<StudyMember> studyMemberList = studyMemberRepository.findAll();
        assertThat(studyMemberList).isEmpty();
    }

    @Test
    @DisplayName("여러 스터디를 탈퇴하면, StudyMember 관계가 모두 삭제된다.")
    void deleteStudyMemberAllWhenStudyOut() {
        // given
        Study study1 = Study.ofPublic(createMockMember(), "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(createMockMember(), "스터디 테스트2", "스터디 설명", 10L, IsUse.Y);
        studyRepository.save(study1);
        studyRepository.save(study2);
        studyMemberRepository.save(new StudyMember(member, study1));
        studyMemberRepository.save(new StudyMember(member, study2));
        StudyControllerRequest.DeleteStudyMemberAllRequest request = new StudyControllerRequest.DeleteStudyMemberAllRequest(Arrays.asList(study1.getId(), study2.getId()));

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
        Study study1 = Study.ofPublic(createMockMember(), "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(createMockMember(), "스터디 테스트2", "스터디 설명", 10L, IsUse.Y);
        studyRepository.save(study1);
        studyRepository.save(study2);
        studyMemberRepository.save(new StudyMember(member, study1));
        StudyControllerRequest.DeleteStudyMemberAllRequest request = new StudyControllerRequest.DeleteStudyMemberAllRequest(Arrays.asList(study1.getId(), study2.getId()));

        // when & then
        assertThatThrownBy(() -> service.deleteStudyMemberAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 스터디를 포함하고 있습니다. memberId = " + member.getMemberId());
    }


    private Member createMockMember() {
        return memberRepository.save(Member.builder()
                        .memberId(UUID.randomUUID().toString())
                        .password(UUID.randomUUID().toString())
                        .build());
    }

}