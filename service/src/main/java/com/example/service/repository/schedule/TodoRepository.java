package com.example.service.repository.schedule;

import com.example.service.entity.member.Member;
import com.example.service.entity.schedule.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByMember(Member member);

    int countAllByIdInAndMember_Id(List<Long> todoIdList, Long memberId);

    int deleteAllByIdInAndMember_Id(List<Long> todoIdList, Long memberId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Todo t where t.member = :member")
    int deleteAllByMember(@Param("member") Member member);
}
