package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositorySupport {

    @Query("select s from Schedule s " +
            "join fetch s.scheduleTodoList stl " +
            "join fetch stl.todo " +
            "where s.member.id = :memberId")
    List<Schedule> findAllByMember_IdByJPQL(@Param("memberId") Long memberId);

    @EntityGraph(attributePaths = {"member", "study"})
    List<Schedule> findAllByMemberAndStudy(Member member, Study study);

    @Query("select s from Schedule s " +
            "left join fetch s.scheduleTodoList stl " +
            "left join fetch stl.todo " +
            "where s.id = :id")
    Optional<Schedule> findById(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Schedule s where s.id in (:scheduleIdList)")
    int deleteAllByScheduleIdList(@Param("scheduleIdList") List<Long> scheduleIdList);

    int countAllByIdInAndMember_Id(List<Long> scheduleIdList, Long memberId);

    int deleteAllByIdInAndMember_Id(List<Long> scheduleIdList, Long memberId);
}
