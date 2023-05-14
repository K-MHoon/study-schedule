package com.example.studyschedule.service.schedule;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class TodoServiceTest extends TestHelper {

    @Autowired
    TodoService todoService;

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        List<Todo> todoList = createTestTodosAndSaveByCount(member, 3);

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember();

        assertAll(() -> assertThat(result).hasSize(3),
                () -> assertThat(result).extracting("id")
                        .containsExactlyInAnyOrderElementsOf(todoList.stream().map(Todo::getId).collect(Collectors.toList())));
    }

    @Test
    @DisplayName("스케줄과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedSchedule() {
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
        String title = "제목 테스트";
        String content = "내용 테스트";
        TodoControllerRequest.CreateTodoRequest request = new TodoControllerRequest.CreateTodoRequest(title, content);

        Todo result = todoService.createTodo(request);

        assertAll(() -> assertThat(result.getTitle()).isEqualTo(title),
                () -> assertThat(result.getContent()).isEqualTo(content));
    }

    @Test
    @DisplayName("할 일 목록을 삭제한다.")
    void deleteTodoAll() {
        List<Todo> testTodoList = createTestTodosAndSaveByCount(member, 3);
        List<Long> testTodoIdList = testTodoList.stream().map(Todo::getId).collect(Collectors.toList());
        TodoControllerRequest.DeleteTodoRequest deleteTodoRequest = new TodoControllerRequest.DeleteTodoRequest(testTodoIdList);

        todoService.deleteTodoAll(deleteTodoRequest);

        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(0);
    }

    @Test
    @DisplayName("멤버에 해당되지 않는 할 일 목록 삭제를 요청할 경우 예외가 발생한다.")
    void doNotDeleteWhenDeleteRequestOtherMemberTodo() {
        // given
        Member otherMember = memberHelper.createSimpleMember("testMember2");
        List<Todo> otherMemberTodoList = createTestTodosAndSaveByCount(otherMember, 1);
        List<Long> otherMemberTodoIdList = otherMemberTodoList.stream().map(Todo::getId).collect(Collectors.toList());

        List<Todo> testTodoList = createTestTodosAndSaveByCount(member, 3);
        List<Long> testTodoIdList = testTodoList.stream().map(Todo::getId).collect(Collectors.toList());

        testTodoIdList.addAll(otherMemberTodoIdList);

        TodoControllerRequest.DeleteTodoRequest deleteTodoRequest = new TodoControllerRequest.DeleteTodoRequest(testTodoIdList);

        // when & then
        assertThatThrownBy(() -> todoService.deleteTodoAll(deleteTodoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 할 일을 포함하고 있습니다. memberId = testMember");

    }
}