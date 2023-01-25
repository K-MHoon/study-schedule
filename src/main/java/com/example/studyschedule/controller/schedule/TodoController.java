package com.example.studyschedule.controller.schedule;

import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import com.example.studyschedule.service.schedule.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/member/{member_id}")
    public ResponseEntity<List<TodoDto>> getMemberTodoList(@PathVariable(name = "member_id") Long memberId) {
        log.info("[getMemberTodoList] call, memberId = {}", memberId);

        List<TodoDto> response = todoService.getTodoDtoListLinkedMember(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/schedule/{schedule_id}")
    public ResponseEntity<List<TodoDto>> getScheduleTodoList(@PathVariable(name = "schedule_id") Long scheduleId) {
        log.info("[getScheduleTodoList] call, scheduleId = {}", scheduleId);

        List<TodoDto> response = todoService.getTodoDtoListLinkedSchedule(scheduleId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/member/{member_id}")
    public ResponseEntity createTodo(@PathVariable(name = "member_id") Long memberId,
    @RequestBody @Valid TodoControllerRequest.CreateTodoRequest request) {
        log.info("[createTodo] call, memberId = {}", memberId);

        todoService.createTodo(memberId, request);

        return new ResponseEntity(HttpStatus.OK);
    }
}
