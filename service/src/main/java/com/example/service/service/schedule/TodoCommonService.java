package com.example.service.service.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Todo;
import com.example.common.repository.schedule.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        List<Long> normalIdList = normalList.stream().map(Todo::getId).collect(Collectors.toList());
        List<Long> targetIdList = targetList.stream().map(Todo::getId).collect(Collectors.toList());

        return !targetIdList.stream()
                .filter(l -> !normalIdList.contains(l))
                .findAny()
                .isPresent();
    }
}
