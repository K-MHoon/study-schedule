package com.example.service.service.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.ScheduleTodo;
import com.example.common.entity.schedule.Todo;
import com.example.common.model.dto.schedule.TodoDto;
import com.example.service.controller.request.schedule.TodoControllerRequest;
import com.example.common.repository.schedule.ScheduleTodoRepository;
import com.example.common.repository.schedule.TodoRepository;
import com.example.service.service.member.MemberCommonService;
import com.example.service.service.schedule.request.TodoServiceRequest;
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
    private final ScheduleTodoRepository scheduleTodoRepository;

    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<TodoDto> getTodoDtoListLinkedMember() {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<Todo> todoList = todoCommonService.getTodoListLinkedMember(loggedInMember);
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
    public Todo createTodo(TodoServiceRequest.CreateTodo request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        Todo newTodo = new Todo(request.getTitle(), request.getContent(), loggedInMember);
        return todoRepository.save(newTodo);
    }

    @Transactional
    public void deleteTodoAll(TodoServiceRequest.DeleteTodo request) {
        Member member = memberCommonService.getLoggedInMember();
        if(isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 할 일을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        List<ScheduleTodo> scheduleTodoList = scheduleTodoRepository.findAllByTodo_IdIn(request.getTodoList());
        if(!scheduleTodoList.isEmpty()) {
            throw new IllegalArgumentException("스케줄이 연결된 할 일이 존재하여 삭제할 수 없습니다.");
        }
        todoRepository.deleteAllByIdInAndMember_Id(request.getTodoList(), member.getId());
    }

    private boolean isNotSameRequestAndDataCount(TodoServiceRequest.DeleteTodo request, Member member) {
        return todoRepository.countAllByIdInAndMember_Id(request.getTodoList(), member.getId()) != request.getTodoList().size();
    }
}
