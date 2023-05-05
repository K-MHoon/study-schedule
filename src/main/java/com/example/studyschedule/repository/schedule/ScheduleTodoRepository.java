package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo, Long> {

    @EntityGraph(attributePaths = {"schedule", "todo"})
    List<ScheduleTodo> findAllByScheduleIn(List<Schedule> schedule);
}
