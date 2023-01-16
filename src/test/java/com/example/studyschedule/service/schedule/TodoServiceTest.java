package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.repository.schedule.TodoRepository;
import com.example.studyschedule.service.member.MemberService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    MemberService memberService;

    @Test
    @DisplayName("파라미터로 모두 빈 배열을 받을 경우 false를 반환한다.")
    void checkTargetTodoListInNormalTodoListWhenParameterAllEmpty() {
        boolean result = todoService.checkTargetTodoListInNormalTodoList(new ArrayList<>(), new ArrayList<>());

        assertEquals(false, result);
    }

    @Test
    @DisplayName("target 배열은 비어있고, normal 배열은 비어있지 않은 경우, false를 반환한다.")
    void checkTargetTodoListInNormalTodoListWhenTargetArrayEmpty() {
        boolean result = todoService.checkTargetTodoListInNormalTodoList(new ArrayList<>(), Arrays.asList(new Todo(), new Todo()));

        assertEquals(false, result);
    }

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        Long memberId = 1L;

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember(memberId);
        entityManager.clear();
        Member member = memberService.validateExistedMemberId(memberId);
        List<Todo> compareList = todoRepository.findAllByMember(member);

        assertEquals(compareList.size(), result.size());
    }

}