package com.example.studyschedule.service.schedule;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TodoServiceTest extends TestHelper {

    @Autowired
    TodoService todoService;

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        Member member = createSimpleMember();
        List<Todo> todoList = createTestTodosAndSaveByCount(member, 3);

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember(member.getId());

        assertAll(() -> assertThat(result).hasSize(3),
                () -> assertThat(result).extracting("id")
                        .containsExactlyInAnyOrderElementsOf(todoList.stream().map(Todo::getId).collect(Collectors.toList())));
    }

    @Test
    @DisplayName("스케줄과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedSchedule() {
        Member member = createSimpleMember();
        List<Schedule> scheduleList = createTestSchedulesAndSaveByCount(member, 1);
        List<Todo> todoList = createTestTodosAndSaveByCount(member, 3);
        connectScheduleTodoList(scheduleList.get(0), todoList);
        entityManagerFlushAndClear();

        List<TodoDto> result = todoService.getTodoDtoListLinkedSchedule(scheduleList.get(0).getId());

        assertAll(() -> assertThat(result).hasSize(3),
                () -> assertThat(result).extracting("id")
                        .containsExactlyInAnyOrderElementsOf(todoList.stream().map(Todo::getId).collect(Collectors.toList())));
    }

    @Test
    @DisplayName("새로운 할 일을 생성한다.")
    void createTodo() {
        Member member = createSimpleMember();
        String title = "제목 테스트";
        String content = "내용 테스트";
        TodoControllerRequest.CreateTodoRequest request = new TodoControllerRequest.CreateTodoRequest(title, content);

        Todo result = todoService.createTodo(member.getId(), request);

        assertAll(() -> assertThat(result.getTitle()).isEqualTo(title),
                () -> assertThat(result.getContent()).isEqualTo(content));
    }

}