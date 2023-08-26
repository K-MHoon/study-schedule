package com.example.service.service.study;

import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyCode;
import com.example.common.repository.study.StudyCodeRepository;
import com.example.service.TestHelper;
import com.example.service.controller.response.study.StudyCodeControllerResponse;
import com.example.service.service.study.request.StudyCodeServiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StudyCodeServiceTest extends TestHelper {

    @Autowired
    StudyCodeService service;

    @Autowired
    StudyCodeRepository studyCodeRepository;

    @Test
    @DisplayName("비밀 스터디에 신규 초대 코드를 발급한다.")
    void createInviteCodeBySecretStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        study.changeToPrivate("test1234");

        // when
        service.createInviteCode(study.getId());

        // then
        List<StudyCode> studyCode = studyCodeRepository.findAll();
        assertThat(studyCode).hasSize(1);
        assertThat(studyCode.get(0).getStudy().getId()).isEqualTo(study.getId());
        assertThat(studyCode.get(0).getUseMember()).isNull();
    }

    @Test
    @DisplayName("비밀 스터디가 아닌 경우 초대 코드 생성시 예외가 발생한다.")
    void causeExceptionWhenCreateInviteCodeIfNotSecretStudy() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);

        // when & then
        assertThatThrownBy(() -> service.createInviteCode(study.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀 스터디만 초대 코드 생성이 가능합니다.");
    }


    @Test
    @DisplayName("요청 받은 스터디 코드 목록을 정상적으로 삭제한다.")
    void deleteInviteCodeAll() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        simpleStudy.changeToPrivate("password");

        StudyCode studyCode1 = studyCodeHelper.createStudyCode(member, simpleStudy);
        StudyCode studyCode2 = studyCodeHelper.createStudyCode(member, simpleStudy);
        entityManagerFlushAndClear();

        StudyCodeServiceRequest.DeleteInviteCodeAll request = StudyCodeServiceRequest.DeleteInviteCodeAll
                .builder()
                .inviteCodeList(Arrays.asList(studyCode1.getId(), studyCode2.getId()))
                .build();

        // when
        service.deleteInviteCodeAll(simpleStudy.getId(), request);

        // then
        List<StudyCode> result = studyCodeRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("스터디가 비밀 스터디가 아닌 경우 예외가 발생한다.")
    void causeExceptionIfStudyIsNotSecret() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        StudyCodeServiceRequest.DeleteInviteCodeAll request = StudyCodeServiceRequest.DeleteInviteCodeAll
                .builder()
                .inviteCodeList(Collections.emptyList())
                .build();

        // when & then
        assertThatThrownBy(() -> service.deleteInviteCodeAll(simpleStudy.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청한 스터디는 비밀 스터디가 아닙니다.");
    }

    @Test
    @DisplayName("스터디 코드가 없거나, 다른 스터디 코드를 요청하는 경우 예외가 발생한다.")
    void causeExceptionWhenRequestOtherStudyInviteCodeOrNotSavedStudyCode() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        simpleStudy.changeToPrivate("password");
        StudyCodeServiceRequest.DeleteInviteCodeAll request = StudyCodeServiceRequest.DeleteInviteCodeAll
                .builder()
                .inviteCodeList(Arrays.asList(Long.MAX_VALUE))
                .build();

        // when & then
        assertThatThrownBy(() -> service.deleteInviteCodeAll(simpleStudy.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디에 존재하지 않는 스터디 코드가 포함되어 있습니다.");
    }

    @Test
    @DisplayName("스터디 코드 목록을 조회한다.")
    void getStudyCodeList() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);
        studyCodeHelper.createStudyCode(null, study);
        studyCodeHelper.createStudyCode(null, study);
        studyCodeHelper.createStudyCode(null, study);

        // when
        StudyCodeControllerResponse.GetStudyCodeListResponse result = service.getInviteCodeList(study.getId());

        // then
        assertThat(result.getStudyCodeList()).hasSize(3);
    }

    @Test
    @DisplayName("스터디 코드가 존재하지 않으면, 빈 배열을 반환한다.")
    void studyCodeIsEmptyWhenNotExistStudyCode() {
        // given
        Study study = studyHelper.createStudyWithStudyMember(member);

        // when
        StudyCodeControllerResponse.GetStudyCodeListResponse result = service.getInviteCodeList(study.getId());

        // then
        assertThat(result.getStudyCodeList()).isEmpty();
    }
}