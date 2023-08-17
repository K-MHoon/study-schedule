package com.example.common.entity.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {


    @Test
    @DisplayName("회원 나이를 업데이트 한다.")
    void updateAge() {
        // given
        int age = 1;

        // when
        Member member = Member.builder()
                .age(age)
                .build();

        // then
        assertThat(member.getAge()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원 나이는 1보다 작을 수 없다.")
    void updateAgeLessThan1() {
        // given
        int age = 0;

        // when // then
        assertThatThrownBy(() -> Member.builder()
                .age(age)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("나이는 1~100살 까지만 입력이 가능합니다.");
    }

    @Test
    @DisplayName("회원 나이는 100보다 클 수 없다.")
    void updateAgeBiggerThan100() {
        // given
        int age = 101;

        // when // then
        assertThatThrownBy(() -> Member.builder()
                .age(age)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("나이는 1~100살 까지만 입력이 가능합니다.");
    }

    @Test
    @DisplayName("회원 나이는 null 일 수 없다.")
    void updateAgeNull() {
        // given
        Integer age = null;

        // when // then
        assertThatThrownBy(() -> Member.builder()
                .age(age)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("나이는 null 값 일 수 없습니다.");
    }


}