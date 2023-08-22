package com.example.service.service.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.Todo;
import com.example.common.model.dto.schedule.TodoDto;
import com.example.common.repository.schedule.TodoRepository;
import com.example.service.TestHelper;
import com.example.service.service.schedule.request.TodoServiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class TodoServiceTest extends TestHelper {

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        List<Todo> todoList = todoHelper.createTestTodosAndSaveByCount(member, 3);

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember();

        assertThat(result).hasSize(3)
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(todoList.stream().map(Todo::getId).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("스케줄과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedSchedule() {
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 1);
        List<Todo> todoList = todoHelper.createTestTodosAndSaveByCount(member, 3);
        scheduleTodoHelper.connectScheduleTodoList(scheduleList.get(0), todoList);
        entityManagerFlushAndClear();

        List<TodoDto> result = todoService.getTodoDtoListLinkedSchedule(scheduleList.get(0).getId());

        assertAll(() -> assertThat(result).hasSize(3)
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(todoList.stream().map(Todo::getId).collect(Collectors.toList())));
    }

    @Test
    @DisplayName("새로운 할 일을 생성한다.")
    void createTodo() {
        TodoServiceRequest.CreateTodo request = TodoServiceRequest.CreateTodo.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .build();

        Todo result = todoService.createTodo(request);

        assertThat(result)
                .extracting("title", "content")
                .contains("제목 테스트", "내용 테스트");
    }

    @Test
    @DisplayName("할 일 목록을 삭제한다.")
    void deleteTodoAll() {
        List<Todo> testTodoList = todoHelper.createTestTodosAndSaveByCount(member, 3);
        List<Long> testTodoIdList = testTodoList.stream().map(Todo::getId).collect(Collectors.toList());
        TodoServiceRequest.DeleteTodo request = TodoServiceRequest.DeleteTodo.builder()
                .todoList(testTodoIdList)
                .build();

        todoService.deleteTodoAll(request);

        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(0);
    }

    @Test
    @DisplayName("스케줄에 연결된 할 일이 있는 경우 예외가 발생한다.")
    void causeExceptionWhenHasScheduleConnectedTodo() {
        // given
        Todo todo = todoHelper.createSimpleTodo(member);
        Schedule simpleSchedule = scheduleHelper.createSimpleSchedule(member);
        scheduleTodoHelper.connectScheduleTodo(simpleSchedule, todo);

        TodoServiceRequest.DeleteTodo request = TodoServiceRequest.DeleteTodo.builder()
                .todoList(List.of(todo.getId()))
                .build();

        // when & then
        assertThatThrownBy(() -> todoService.deleteTodoAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스케줄이 연결된 할 일이 존재하여 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("멤버에 해당되지 않는 할 일 목록 삭제를 요청할 경우 예외가 발생한다.")
    void doNotDeleteWhenDeleteRequestOtherMemberTodo() {
        // given
        Member otherMember = memberHelper.createSimpleMember("testMember2");
        List<Todo> otherMemberTodoList = todoHelper.createTestTodosAndSaveByCount(otherMember, 1);
        List<Long> otherMemberTodoIdList = otherMemberTodoList.stream().map(Todo::getId).collect(Collectors.toList());

        List<Todo> testTodoList = todoHelper.createTestTodosAndSaveByCount(member, 3);
        List<Long> testTodoIdList = testTodoList.stream().map(Todo::getId).collect(Collectors.toList());
        testTodoIdList.addAll(otherMemberTodoIdList);

        TodoServiceRequest.DeleteTodo request = TodoServiceRequest.DeleteTodo.builder()
                .todoList(testTodoIdList)
                .build();

        // when & then
        assertThatThrownBy(() -> todoService.deleteTodoAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 할 일을 포함하고 있습니다. memberId = testMember");

    }
}