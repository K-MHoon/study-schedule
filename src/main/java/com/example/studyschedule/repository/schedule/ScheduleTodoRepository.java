package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.ScheduleTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo, Long> {
}
