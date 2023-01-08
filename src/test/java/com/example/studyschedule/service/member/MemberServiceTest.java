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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        String name = "흑시바";
        Integer age = 10;
        MemberControllerRequest.CreateMemberRequest request = new MemberControllerRequest.CreateMemberRequest(name, age);

        Member member = memberService.createMember(request);
        entityManager.clear();
        Member findMember = memberRepository.findById(member.getId()).get();

        assertEquals(name, findMember.getName());
    }
}