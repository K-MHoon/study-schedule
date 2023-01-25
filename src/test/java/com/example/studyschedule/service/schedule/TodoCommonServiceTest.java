package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.schedule.Todo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoCommonServiceTest {

    @Autowired
    TodoCommonService todoCommonService;

    @Test
    @DisplayName("파라미터로 모두 빈 배열을 받을 경우 false를 반환한다.")
    void checkTargetTodoListInNormalTodoListWhenParameterAllEmpty() {
        boolean result = todoCommonService.checkTargetTodoListInNormalTodoList(new ArrayList<>(), new ArrayList<>());

        assertEquals(false, result);
    }

    @Test
    @DisplayName("target 배열은 비어있고, normal 배열은 비어있지 않은 경우, false를 반환한다.")
    void checkTargetTodoListInNormalTodoListWhenTargetArrayEmpty() {
        boolean result = todoCommonService.checkTargetTodoListInNormalTodoList(new ArrayList<>(), Arrays.asList(Todo.NoArgsInstance(), Todo.NoArgsInstance()));

        assertEquals(false, result);
    }
}