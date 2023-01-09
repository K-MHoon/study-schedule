package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
