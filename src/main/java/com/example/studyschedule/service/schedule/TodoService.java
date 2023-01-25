package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import com.example.studyschedule.repository.schedule.TodoRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {
    private final MemberCommonService memberCommonService;
    private final TodoCommonService todoCommonService;
    private final ScheduleCommonService scheduleCommonService;

    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<TodoDto> getTodoDtoListLinkedMember(Long memberId) {
        Member member = memberCommonService.validateExistedMemberId(memberId);
        List<Todo> todoList = todoCommonService.getTodoListLinkedMember(member);
        return todoList.stream()
                .map(TodoDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoDto> getTodoDtoListLinkedSchedule(Long scheduleId) {
        Schedule schedule = scheduleCommonService.validateExistedScheduleId(scheduleId);
        return schedule.getScheduleTodoList().stream()
                .map(ScheduleTodo::getTodo)
                .map(TodoDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Todo createTodo(Long memberId, TodoControllerRequest.CreateTodoRequest request) {
        Member member = memberCommonService.validateExistedMemberId(memberId);
        Todo newTodo = new Todo(request.getTitle(), request.getContent(), member);
        return todoRepository.save(newTodo);
    }
}
