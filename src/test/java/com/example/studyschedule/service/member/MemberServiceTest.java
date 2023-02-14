package com.example.studyschedule.service.member;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.model.request.member.MemberControllerRequest;
import com.example.studyschedule.repository.member.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 전체 목록을 조회한다.")
    public void getMemberList(){
        List<Member> memberList = memberRepository.findAll();

        List<MemberDto> response = memberService.getMemberList();

        assertEquals(memberList.size(), response.size());
    }

    @Test
    @DisplayName("회원 단일 정보를 조회한다.")
    public void getMember() {
        Member target = memberRepository.findById(1L).get();

        MemberDto member = memberService.getMember(1L);

        assertEquals(target.getName(), member.getName());
    }

    @Test
    @DisplayName("존재하지 않는 회원의 정보를 요청할 경우 예외를 발생시킨다.")
    public void causedExceptionWhenNotFoundMemberId() {
        assertThrows(EntityNotFoundException.class, () -> memberService.getMember(99999L));
    }

    @Test
    @DisplayName("새로운 스터디 회원을 생성한다.")
    public void createMember() {
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
    public void causeExceptionWhenRequestAlreadyExistedMemberIdCreate() {
        String memberId = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        String name = "흑시바";
        Integer age = 10;
        MemberControllerRequest.CreateMemberRequest request = new MemberControllerRequest.CreateMemberRequest(memberId, password, name, age);

        memberService.createMember(request);
        entityManager.clear();

        assertThrows(IllegalArgumentException.class, () -> memberService.createMember(request));
    }
}