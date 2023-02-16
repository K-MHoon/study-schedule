package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.request.schedule.TodoControllerRequest;
import com.example.studyschedule.repository.schedule.TodoRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberCommonService memberCommonService;

    @Autowired
    ScheduleCommonService scheduleCommonService;

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        Long memberId = 1L;

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember(memberId);
        entityManager.clear();
        Member member = memberCommonService.validateExistedMemberById(memberId);
        List<Todo> compareList = todoRepository.findAllByMember(member);

        assertEquals(compareList.size(), result.size());
    }

    @Test
    @DisplayName("스케줄과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedSchedule() {
        Long scheduleId = 1L;

        List<TodoDto> result = todoService.getTodoDtoListLinkedSchedule(scheduleId);
        entityManager.clear();
        Schedule schedule = scheduleCommonService.validateExistedScheduleId(scheduleId);

        assertEquals(schedule.getScheduleTodoList().size(), result.size());
    }

    @Test
    @DisplayName("새로운 할 일을 생성한다.")
    void createTodo() {
        Long memberId = 1L;
        String title = "제목 테스트";
        String content = "내용 테스트";
        TodoControllerRequest.CreateTodoRequest request = new TodoControllerRequest.CreateTodoRequest(title, content);

        Todo response = todoService.createTodo(memberId, request);

        assertAll(() -> assertEquals(title, response.getTitle()),
                () -> assertEquals(content, response.getContent()));
    }

}