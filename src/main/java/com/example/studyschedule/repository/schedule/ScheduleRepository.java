package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
