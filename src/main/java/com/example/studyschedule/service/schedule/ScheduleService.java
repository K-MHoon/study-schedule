package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.service.member.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;
    private final TodoService todoService;
    private final ScheduleTodoService scheduleTodoService;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getMemberScheduleList(Long memberId) {
        memberService.validateExistedMemberId(memberId);
        return scheduleRepository.findAllByMember_Id(memberId).stream()
                .map(ScheduleDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto getSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 스케줄을 찾을 수 없습니다."));
        return ScheduleDto.entityToDto(schedule);
    }

    @Transactional
    public Schedule createSchedule(Long memberId, ScheduleControllerRequest.CreateScheduleRequest request) {
        Member member = memberService.validateExistedMemberId(memberId);
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
        }
        Schedule newSchedule = new Schedule(member, request.getStartDate(), request.getEndDate(), request.getIsUse());
        scheduleRepository.save(newSchedule);

        createScheduleTodo(request.getTodoList(), member, newSchedule);
        return newSchedule;
    }

    private void createScheduleTodo(List<Long> targetIdList, Member member, Schedule newSchedule) {
        List<Todo> target = todoService.getTodoListByIdList(targetIdList);
        List<Todo> todoListLinkedMember = todoService.getTodoListLinkedMember(member);

        if(todoService.checkTargetTodoListInNormalTodoList(target, todoListLinkedMember)) {
            scheduleTodoService.createScheduleTodo(newSchedule, target);
        }
    }

}