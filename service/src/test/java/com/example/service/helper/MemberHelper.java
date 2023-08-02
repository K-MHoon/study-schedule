package com.example.service.helper;

import com.example.common.entity.member.Member;
import com.example.common.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Transactional
public class MemberHelper {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> createTestMembersAndSaveByCount(int count) {
        return createTestMembersAndSaveByCount(0, count);
    }

    public List<Member> createTestMembersAndSaveByCount(int start, int count) {
        return IntStream.range(start, count)
                .mapToObj(c -> {
                    Member member = Member.builder().memberId("testMember" + c).password("testPassword").build();
                    return memberRepository.save(member);
                })
                .collect(Collectors.toList());
    }

    public Member createSimpleMember() {
        Member member = Member.builder().memberId("testMember").password("testPassword").build();
        return memberRepository.save(member);
    }

    public Member createSimpleMember(String memberId) {
        Member member = Member.builder().memberId(memberId).password("testPassword").build();
        return memberRepository.save(member);
    }

    public Member find(String memberId) {
        return memberRepository.findByMemberId(memberId).orElseThrow(() -> new EntityNotFoundException("해당하는 멤버 ID가 존재하지 않습니다."));
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Member getUnknownMember() {
        return memberRepository.save(Member.builder()
                .memberId(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .build());
    }
}
