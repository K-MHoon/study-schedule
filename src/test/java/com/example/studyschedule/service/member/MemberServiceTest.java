package com.example.studyschedule.service.member;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

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
        createTestMembersAndSaveByCount(2);

        List<MemberDto> response = memberService.getMemberList();

        assertAll(() -> assertThat(response).hasSize(2),
                () -> assertThat(response).extracting("memberId").containsExactlyInAnyOrder("testMember0", "testMember1"));
    }

    @Test
    @DisplayName("회원 단일 정보를 조회한다.")
    void getMember() {
        List<Member> memberList = createTestMembersAndSaveByCount(3);

        MemberDto member = memberService.getMemberById(memberList.get(0).getId());

        assertThat(member.getId()).isEqualTo(memberList.get(0).getId());
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
        String memberId = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        String name = "흑시바";
        Integer age = 10;
        MemberControllerRequest.CreateMemberRequest request = new MemberControllerRequest.CreateMemberRequest(memberId, password, name, age);

        memberService.createMember(request);
        entityManager.clear();

        Member findMember = memberRepository.findByMemberId(memberId).get();

        assertAll(() -> assertThat(findMember.getMemberId()).isEqualTo(memberId),
                () -> assertThat(passwordEncoder.matches(password, findMember.getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("기존에 있는 MemberId 생성을 요청하면 예외가 발생한다.")
    void causeExceptionWhenRequestAlreadyExistedMemberIdCreate() {
        String memberId = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        String name = "흑시바";
        Integer age = 10;
        MemberControllerRequest.CreateMemberRequest request = new MemberControllerRequest.CreateMemberRequest(memberId, password, name, age);

        memberService.createMember(request);
        entityManager.clear();

        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                        .extracting("message")
                                .isEqualTo("동일한 멤버가 존재합니다. ID = " + memberId);
    }

    @Test
    @DisplayName("회원정보 이름, 나이를 변경한다.")
    @WithMockUser(username = "testMember")
    void updateUserNameAndAge() {
        Member member = createSimpleMember();
        MemberControllerRequest.UpdateMemberProfileRequest request = new MemberControllerRequest.UpdateMemberProfileRequest("홍길동", 33);

        memberService.updateMemberProfile(request);

        assertThat(member.getName()).isEqualTo("홍길동");
        assertThat(member.getAge()).isEqualTo(33);
    }
}