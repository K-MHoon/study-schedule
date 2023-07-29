package com.example.studyschedule.helper;


import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.repository.schedule.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Transactional
public class TodoHelper {

    @Autowired
    TodoRepository todoRepository;

    public Optional<Todo> findById(Long todoId) {
        return todoRepository.findById(todoId);
    }

    public List<Todo> createTestTodosAndSaveByCount(Member member, int count) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Todo todo = new Todo("test Title " + c, "test Content " + c, member);
                    return todoRepository.save(todo);
                })
                .collect(Collectors.toList());
    }

    public Todo createSimpleTodo(Member member) {
        Todo todo = Todo.builder()
                .title("simple Title")
                .content("simple Content")
                .member(member)
                .build();
        return todoRepository.save(todo);
    }
}
