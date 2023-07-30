package com.example.service.service.schedule;

import com.example.service.entity.schedule.Schedule;
import com.example.service.entity.schedule.ScheduleTodo;
import com.example.service.entity.schedule.Todo;
import com.example.service.repository.schedule.ScheduleTodoRepository;
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
        if(todoList.isEmpty()) {
            return;
        }
        List<ScheduleTodo> newScheduleTodoList = todoList.stream().map(todo -> new ScheduleTodo(schedule, todo)).collect(Collectors.toList());
        scheduleTodoRepository.saveAll(newScheduleTodoList);
    }

    @Transactional(readOnly = true)
    public List<ScheduleTodo> getScheduleTodoList(List<Schedule> scheduleList) {
        return scheduleTodoRepository.findAllByScheduleIn(scheduleList);
    }

    @Transactional
    public void removeAllScheduleTodo(List<ScheduleTodo> scheduleTodoList) {
        scheduleTodoRepository.deleteAll(scheduleTodoList);
    }
}
