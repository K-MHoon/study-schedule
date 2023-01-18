package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.repository.schedule.TodoRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    MemberCommonService memberCommonService;

    @Test
    @DisplayName("스터디 회원과 연결된 Todo 정보를 가지고 온다.")
    void getTodoDtoListLinkedMember() {
        Long memberId = 1L;

        List<TodoDto> result = todoService.getTodoDtoListLinkedMember(memberId);
        entityManager.clear();
        Member member = memberCommonService.validateExistedMemberId(memberId);
        List<Todo> compareList = todoRepository.findAllByMember(member);

        assertEquals(compareList.size(), result.size());
    }

}