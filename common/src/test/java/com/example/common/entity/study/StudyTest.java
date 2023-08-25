package com.example.common.entity.study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    @DisplayName("비밀 스터디로 전환한다.")
    void changeToPrivate() {
        // given
        Study study = Study.builder()
                .secret(Boolean.FALSE)
                .build();

        // when
        study.changeToPrivate("testPassword");

        // then
        assertThat(study)
                .extracting("secret", "password")
                .contains(Boolean.TRUE, "testPassword");
    }

    @Test
    @DisplayName("비밀 스터디는 반드시 비밀번호가 입력되어야 한다.")
    void changeToPrivateNonePassword() {
        // given
        Study study = Study.builder()
                .secret(Boolean.FALSE)
                .build();

        // when // then
        assertThatThrownBy(() -> study.changeToPrivate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀 스터디에는 반드시 비밀번호가 포함되어야 합니다.");
    }
}