package com.example.studyschedule.controller.schedule;

import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import com.example.studyschedule.service.schedule.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getMemberTodoList(Principal principal) {
        log.info("[getMemberTodoList] called by {}", principal.getName());

        return  todoService.getTodoDtoListLinkedMember();
    }

    @GetMapping("/schedule/{schedule_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getScheduleTodoList(@PathVariable(name = "schedule_id") Long scheduleId) {
        log.info("[getScheduleTodoList] call, scheduleId = {}", scheduleId);

        return todoService.getTodoDtoListLinkedSchedule(scheduleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createTodo(Principal principal,
    @RequestBody @Validated TodoControllerRequest.CreateTodoRequest request) {
        log.info("[createTodo] called by {} ", principal.getName());

        todoService.createTodo(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodoAll(@RequestBody @Validated TodoControllerRequest.DeleteTodoRequest request,
                                  Principal principal) {
        log.info("[deleteTodoAll] called by {}, todo List = {}", principal.getName(), request.getTodoList());

        todoService.deleteTodoAll(request);
    }
}
