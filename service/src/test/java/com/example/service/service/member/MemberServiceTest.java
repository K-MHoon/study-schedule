package com.example.service.service.member;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.Todo;
import com.example.common.entity.study.Study;
import com.example.common.model.dto.member.MemberDto;
import com.example.service.TestHelper;
import com.example.service.service.request.MemberServiceRequest;
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
        MemberServiceRequest.CreateMember request = createSimpleMemberRequest();

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
        MemberServiceRequest.CreateMember request = createSimpleMemberRequest();

        // when
        memberService.createMember(request);

        // then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                        .extracting("message")
                                .isEqualTo("동일한 멤버가 존재합니다. ID = " + request.getMemberId());
    }

    private MemberServiceRequest.CreateMember createSimpleMemberRequest() {
        return MemberServiceRequest.CreateMember.builder()
                .memberId(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .name("흑시바")
                .age(10)
                .build();
    }

    @Test
    @DisplayName("회원정보 이름, 나이, 비밀번호를 변경한다.")
    void updateUserNameAndAge() {
        // given
        MemberServiceRequest.UpdateMemberProfile request = MemberServiceRequest.UpdateMemberProfile
                .builder()
                .name("홍길동")
                .password("1234")
                .age(33)
                .build();
        String prevPassword = member.getPassword();

        // when
        memberService.updateMemberProfile(request);

        // then
        assertThat(prevPassword).isNotEqualTo(member.getPassword());
        assertThat(passwordEncoder.matches("1234", member.getPassword())).isTrue();
        assertThat(member.getName()).isEqualTo("홍길동");
        assertThat(member.getAge()).isEqualTo(33);
    }

    @Test
    @DisplayName("비밀번호가 없는 경우, 현재 비밀번호가 유지된다.")
    void maintainUserPasswordWhenRequestPasswordIsEmpty() {
        // given
        MemberServiceRequest.UpdateMemberProfile request = MemberServiceRequest.UpdateMemberProfile
                .builder()
                .name("홍길동")
                .age(33)
                .build();
        String prevPassword = member.getPassword();

        // when
        memberService.updateMemberProfile(request);

        // then
        assertThat(prevPassword).isEqualTo(member.getPassword());
    }

    @Test
    @DisplayName("스터디를 생성한 회원을 삭제하려는 경우, 예외가 발생한다.")
    void causeExceptionWhenDeleteMemberIfHasStudy() {
        // given
        Study simpleStudy = studyHelper.createSimpleStudy(member);

        // when & then
        assertThatThrownBy(() -> memberService.deleteMember())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영중인 스터디가 존재하여 탈퇴할 수 없습니다.");
    }

    @Test
    @DisplayName("스터디가 가입된 회원을 삭제하려는 경우, 예외가 발생한다.")
    void causeExceptionWhenDeleteMemberIfHasJoinedStudy() {
        // given
        Member otherMember = memberHelper.createSimpleMember("otherMember");
        Study study = studyHelper.createStudyWithStudyMember(otherMember);
        studyHelper.joinStudy(study, member);

        // when & then
        assertThatThrownBy(() -> memberService.deleteMember())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가입된 스터디가 존재하여 탈퇴할 수 없습니다. 모든 스터디를 탈퇴해주세요.");
    }

    @Test
    @DisplayName("회원 삭제에 성공한다.")
    void deleteMember() {
        // given
        Schedule simpleSchedule = scheduleHelper.createSimpleSchedule(member);
        Todo simpleTodo = todoHelper.createSimpleTodo(member);
        scheduleTodoHelper.connectScheduleTodo(simpleSchedule, simpleTodo);

        // when
        memberService.deleteMember();

        // then
        assertThat(memberHelper.findById(member.getId())).isEmpty();
        assertThat(scheduleHelper.findById(simpleSchedule.getId())).isEmpty();
        assertThat(todoHelper.findById(simpleTodo.getId())).isEmpty();
    }
}