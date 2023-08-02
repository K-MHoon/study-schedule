package com.example.service.helper;

import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.ScheduleTodo;
import com.example.common.entity.schedule.Todo;
import com.example.common.repository.schedule.ScheduleTodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class ScheduleTodoHelper {

    @Autowired
    protected ScheduleTodoRepository scheduleTodoRepository;

    public List<ScheduleTodo> connectScheduleTodoList(Schedule schedule, List<Todo> todoList) {
        return todoList.stream().map(todo -> {
                    ScheduleTodo scheduleTodo = new ScheduleTodo(schedule, todo);
                    return scheduleTodoRepository.save(scheduleTodo);
                })
                .collect(Collectors.toList());
    }

    public ScheduleTodo connectScheduleTodo(Schedule schedule, Todo todo) {
        ScheduleTodo scheduleTodo = new ScheduleTodo(schedule, todo);
        return scheduleTodoRepository.save(scheduleTodo);
    }
}
