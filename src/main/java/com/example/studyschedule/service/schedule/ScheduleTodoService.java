package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.repository.schedule.ScheduleTodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleTodoService {

    private final ScheduleTodoRepository scheduleTodoRepository;

    @Transactional
    public void createScheduleTodo(Schedule schedule, List<Todo> todoList) {
        List<ScheduleTodo> newScheduleTodoList = todoList.stream().map(todo -> new ScheduleTodo(schedule, todo)).collect(Collectors.toList());
        scheduleTodoRepository.saveAll(newScheduleTodoList);
    }

    @Transactional(readOnly = true)
    public List<ScheduleTodo> getScheduleTodoList(List<Schedule> scheduleList) {
        return scheduleTodoRepository.findAllByScheduleIn(scheduleList);
    }
}
