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
        Member member = memberCommonService.validateExistedMemberById(memberId);
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
        Member member = memberCommonService.validateExistedMemberById(memberId);
        Todo newTodo = new Todo(request.getTitle(), request.getContent(), member);
        return todoRepository.save(newTodo);
    }

    @Transactional
    public void deleteTodoAll(TodoControllerRequest.DeleteTodoRequest request) {
        Member member = memberCommonService.getLoggedInMember();
        if(isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 할 일을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        todoRepository.deleteAllByIdInAndMember_Id(request.getTodoList(), member.getId());

    }

    private boolean isNotSameRequestAndDataCount(TodoControllerRequest.DeleteTodoRequest request, Member member) {
        return todoRepository.countAllByIdInAndMember_Id(request.getTodoList(), member.getId()) != request.getTodoList().size();
    }
}
