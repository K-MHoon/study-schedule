package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s " +
            "join fetch s.scheduleTodoList stl " +
            "join fetch stl.todo " +
            "where s.member.id = :memberId")
    List<Schedule> findAllByMember_IdByJPQL(@Param("memberId") Long memberId);

    List<Schedule> findAllByMember_Id(Long memberId);

    @Query("select s from Schedule s " +
            "join fetch s.scheduleTodoList stl " +
            "join fetch stl.todo " +
            "where s.id = :id")
    Optional<Schedule> findById(@Param("id") Long id);
}
