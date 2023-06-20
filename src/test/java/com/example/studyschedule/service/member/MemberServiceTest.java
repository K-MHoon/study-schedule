package com.example.studyschedule.service.member;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberServiceTest extends TestHelper {

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원 전체 목록을 조회한다.")
    void getMemberList() {
        memberHelper.createTestMembersAndSaveByCount(2);

        List<MemberDto> result = memberService.getMemberList();

        assertAll(() -> assertThat(result).hasSize(3),
                () -> assertThat(result)
                        .extracting("memberId")
                        .containsExactlyInAnyOrder("testMember", "testMember0", "testMember1"));
    }

    @Test
    @DisplayName("회원 단일 정보를 조회한다.")
    void getMember() {
        List<Member> memberList = memberHelper.createTestMembersAndSaveByCount(3);

        MemberDto member = memberService.getMemberById(memberList.get(0).getId());

        assertThat(member.getId()).isEqualTo(memberList.get(0).getId());
    }

    @Test
    @DisplayName("로그인한 회원 정보를 조회한다.")
    void getMemberProfile() {
        // given & when
        MemberDto memberProfile = memberService.getMemberProfile();

        // then
        assertAll(() -> assertThat(memberProfile.getMemberId()).isEqualTo(member.getMemberId()),
                () -> assertThat(memberProfile.getName()).isEqualTo(member.getName()),
                () -> assertThat(memberProfile.getAge()).isEqualTo(member.getAge()),
                () -> assertThat(memberProfile.getId()).isEqualTo(member.getId()));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 정보를 요청할 경우 예외를 발생시킨다.")
    void causedExceptionWhenNotFoundMemberId() {
        assertThatThrownBy(() -> memberService.getMemberById(99999L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("message")
                .isEqualTo("ID에 해당하는 멤버를 찾을 수 없습니다. id = " + 99999);
    }

    @Test
    @DisplayName("새로운 스터디 회원을 생성한다.")
    void createMember() {
        // given
        MemberControllerRequest.CreateMemberRequest request = createSimpleMemberRequest();

        // then
        memberService.createMember(request);

        Member findMember = memberHelper.find(request.getMemberId());
        assertAll(() -> assertThat(findMember.getMemberId()).isEqualTo(request.getMemberId()),
                () -> assertThat(passwordEncoder.matches(request.getPassword(), findMember.getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("기존에 있는 MemberId 생성을 요청하면 예외가 발생한다.")
    void causeExceptionWhenRequestAlreadyExistedMemberIdCreate() {
        // given
        MemberControllerRequest.CreateMemberRequest request = createSimpleMemberRequest();

        // when
        memberService.createMember(request);

        // then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                        .extracting("message")
                                .isEqualTo("동일한 멤버가 존재합니다. ID = " + request.getMemberId());
    }

    private MemberControllerRequest.CreateMemberRequest createSimpleMemberRequest() {
        return MemberControllerRequest.CreateMemberRequest.builder()
                .memberId(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .name("흑시바")
                .age(10)
                .build();
    }

    @Test
    @DisplayName("회원정보 이름, 나이를 변경한다.")
    void updateUserNameAndAge() {
        MemberControllerRequest.UpdateMemberProfileRequest request = new MemberControllerRequest.UpdateMemberProfileRequest("홍길동", 33);

        memberService.updateMemberProfile(request);

        assertThat(member.getName()).isEqualTo("홍길동");
        assertThat(member.getAge()).isEqualTo(33);
    }
}