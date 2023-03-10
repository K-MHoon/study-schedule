package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.repository.schedule.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoCommonService {

    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<Todo> getTodoListLinkedMember(Member member) {
        if(Objects.isNull(member)) {
            return Collections.emptyList();
        }
        return todoRepository.findAllByMember(member);
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodoListByIdList(List<Long> idList) {
        if(Objects.isNull(idList) || idList.isEmpty()) {
            return Collections.emptyList();
        }
        return todoRepository.findAllById(idList);
    }

    @Transactional(readOnly = true)
    public boolean checkTargetTodoListInNormalTodoList(List<Todo> targetList, List<Todo> normalList) {
        if(Objects.isNull(targetList) || Objects.isNull(normalList)) {
            return false;
        }
        if(targetList.isEmpty()) {
            return false;
        }
        if(targetList.size() > normalList.size()) {
            return false;
        }

        return !targetList.stream()
                .filter(todo -> !normalList.contains(todo))
                .findAny()
                .isPresent();
    }
}
