package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByMember(Member member);

    int countAllByIdInAndMember_Id(List<Long> todoIdList, Long memberId);

    int deleteAllByIdInAndMember_Id(List<Long> todoIdList, Long memberId);
}
