package com.example.service.controller.schedule;

import com.example.common.model.dto.schedule.TodoDto;
import com.example.service.controller.request.schedule.TodoControllerRequest;
import com.example.service.service.schedule.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getMemberTodoList() {
        return  todoService.getTodoDtoListLinkedMember();
    }

    @GetMapping("/schedule/{schedule_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getScheduleTodoList(@PathVariable(name = "schedule_id") Long scheduleId) {
        return todoService.getTodoDtoListLinkedSchedule(scheduleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createTodo(@RequestBody @Validated TodoControllerRequest.CreateTodoRequest request) {
        todoService.createTodo(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodoAll(@RequestBody @Validated TodoControllerRequest.DeleteTodoRequest request) {
        todoService.deleteTodoAll(request);
    }
}
