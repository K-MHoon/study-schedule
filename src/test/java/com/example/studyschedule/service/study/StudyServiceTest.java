package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.member.MemberRepository;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@WithMockUser(username = "test", authorities = {"USER"})
class StudyServiceTest {

    @Autowired
    StudyService service;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    private Member member;

    @BeforeEach
    void init() {
        Member member = Member.builder()
                .memberId("test")
                .password("test")
                .name("test")
                .build();
        this.member = memberRepository.save(member);
    }

    @Test
    @DisplayName("?????? ?????? ???????????? ????????? ???????????? ????????????.")
    void getPublicStudyListTest() {
        studyRepository.save(Study.ofPublic(member, "Study Test", 10L, IsUse.Y));
        Pageable pageRequest = PageRequest.of(0, 10);

        Pagination<List<StudyDto>> response = service.getPublicStudyList(pageRequest);

        assertAll(() -> assertThat(response.getData()).hasSize(1),
                () -> assertThat(response.getData().get(0).getStudyName()).isEqualTo("Study Test"),
                () -> assertThat(response.getData().get(0).getLeaderName()).isEqualTo(member.getName()));
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????????.")
    void createPublicStudy() {
        // given
        String studyName = "????????? ?????????";
        Boolean secret = false;
        String password = null;
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;
        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, secret, password, fullCount, isUse);

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
    @DisplayName("?????? ?????? ???????????? ?????? ????????????.")
    void getPublicStudyDetail() {
        // given
        Study study = Study.ofPublic(member, "????????? ?????????", 10L, IsUse.Y);
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        // when
        StudyDto studyDetail = service.getPublicStudyDetail(mockStudy.getId());

        // then
        assertThat(studyDetail.getId()).isEqualTo(mockStudy.getId());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ???????????? ???????????? ?????????.")
    void doNotFindStudyIfStudyIsSecret() {
        // given
        Study study = Study.ofPublic(member, "????????? ?????????", 10L, IsUse.Y);
        study.changeToPrivate("test");
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        // when & then
        assertThrows(EntityNotFoundException.class
                , () -> service.getPublicStudyDetail(mockStudy.getId()));
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ????????????.")
    void createPrivateStudy() {
        // given
        String studyName = "????????? ?????????";
        Boolean secret = true;
        String password = "test";
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;
        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, secret, password, fullCount, isUse);

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
    @DisplayName("????????? ????????? ????????????.")
    void deleteStudy() {
        Study study = Study.ofPublic(member, "????????? ?????????", 10L, IsUse.Y);
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        service.deleteStudy(mockStudy.getId());

        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).isEmpty();
    }

    @Test
    @DisplayName("???????????? ????????? ???, ????????? StudyMember??? ?????? ?????? ??????.")
    void deleteAllLinkedStudyMembersWhenDeleteStudy() {
        Study study = Study.ofPublic(member, "????????? ?????????", 10L, IsUse.Y);
        Study mockStudy = studyRepository.save(study);
        mockStudy.addStudyMember(createMockMember());
        mockStudy.addStudyMember(createMockMember());

        service.deleteStudy(mockStudy.getId());

        List<StudyMember> studyMemberList = studyMemberRepository.findAll();
        assertThat(studyMemberList).isEmpty();
    }

    @Test
    @DisplayName("?????? ???????????? ????????????, StudyMember ????????? ?????? ????????????.")
    void deleteStudyMemberAllWhenStudyOut() {
        // given
        Study study1 = Study.ofPublic(createMockMember(), "????????? ?????????1", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(createMockMember(), "????????? ?????????2", 10L, IsUse.Y);
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
    @DisplayName("???????????? ?????? ????????? ????????? ???????????? ??????, ????????? ????????????.")
    void causeExceptionWhenNotJoinedStudyOutRequest() {
        // given
        Study study1 = Study.ofPublic(createMockMember(), "????????? ?????????1", 10L, IsUse.Y);
        Study study2 = Study.ofPublic(createMockMember(), "????????? ?????????2", 10L, IsUse.Y);
        studyRepository.save(study1);
        studyRepository.save(study2);
        studyMemberRepository.save(new StudyMember(member, study1));
        StudyControllerRequest.DeleteStudyMemberAllRequest request = new StudyControllerRequest.DeleteStudyMemberAllRequest(Arrays.asList(study1.getId(), study2.getId()));

        // when & then
        assertThatThrownBy(() -> service.deleteStudyMemberAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ???????????? ????????? ??? ?????? ???????????? ???????????? ????????????. memberId = " + member.getMemberId());
    }


    private Member createMockMember() {
        return memberRepository.save(Member.builder()
                        .memberId(UUID.randomUUID().toString())
                        .password(UUID.randomUUID().toString())
                        .build());
    }

}