package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.member.MemberRepository;
import com.example.studyschedule.repository.study.StudyRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    private Member member;

    @BeforeEach
    void init() {
        Member member = Member.builder()
                .memberId("test")
                .password("test")
                .build();
        this.member = memberRepository.save(member);
    }

    @Test
    @DisplayName("현재 사용 가능하고 공개된 스터디가 조회된다.")
    void getPublicStudyListTest() {
        Member savedMember = memberRepository.save(new Member("Test", passwordEncoder.encode(UUID.randomUUID().toString()), List.of("USER"), "Test", 100));
        studyRepository.save(Study.ofPublic(savedMember, "Study Test", 10L, IsUse.Y));
        Pageable pageRequest = PageRequest.of(0, 10);

        Pagination<List<StudyDto>> response = service.getPublicStudyList(pageRequest);

        assertAll(() -> assertThat(response.getData()).hasSize(1),
                () -> assertThat(response.getData().get(0).getStudyName()).isEqualTo("Study Test"),
                () -> assertThat(response.getData().get(0).getLeaderName()).isEqualTo("Test"));
    }

    @Test
    @DisplayName("공개 스터디를 정상 생성한다.")
    void createPublicStudy() {
        String studyName = "스터디 테스트";
        Boolean secret = false;
        String password = null;
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;

        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, secret, password, fullCount, isUse);

        service.createPublicStudy(request);

        List<Study> studyList = studyRepository.findAll();

        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getName()).isEqualTo(studyName)
        );
    }

    @Test
    @DisplayName("비공개 스터디를 정상 생성한다.")
    void createPrivateStudy() {
        String studyName = "스터디 테스트";
        Boolean secret = true;
        String password = "test";
        Long fullCount = 10L;
        IsUse isUse = IsUse.Y;

        StudyControllerRequest.CreateStudyRequest request
                = new StudyControllerRequest.CreateStudyRequest(studyName, secret, password, fullCount, isUse);

        service.createPublicStudy(request);

        List<Study> studyList = studyRepository.findAll();

        assertAll(
                () -> assertThat(studyList).hasSize(1),
                () -> assertThat(studyList.get(0).getName()).isEqualTo(studyName),
                () -> assertThat(studyList.get(0).getSecret()).isTrue(),
                () -> assertThat(passwordEncoder.matches(password, studyList.get(0).getPassword())).isTrue()
        );
    }

}