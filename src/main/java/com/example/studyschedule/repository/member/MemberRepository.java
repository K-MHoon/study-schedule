package com.example.studyschedule.repository.member;

import com.example.studyschedule.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
