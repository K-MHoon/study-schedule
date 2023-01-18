package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
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
    private final MemberCommonService memberCommonService;
    private final TodoCommonService todoCommonService;
    private final ScheduleTodoService scheduleTodoService;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getMemberScheduleList(Long memberId) {
        memberCommonService.validateExistedMemberId(memberId);
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
        Member member = memberCommonService.validateExistedMemberId(memberId);
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
        }
        Schedule newSchedule = new Schedule(member, request.getStartDate(), request.getEndDate(), request.getIsUse());
        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        createScheduleTodo(request.getTodoList(), member, savedSchedule);

        return savedSchedule;
    }

    private void createScheduleTodo(List<Long> targetIdList, Member member, Schedule newSchedule) {
        List<Todo> target = todoCommonService.getTodoListByIdList(targetIdList);
        List<Todo> todoListLinkedMember = todoCommonService.getTodoListLinkedMember(member);

        if(todoCommonService.checkTargetTodoListInNormalTodoList(target, todoListLinkedMember)) {
            scheduleTodoService.createScheduleTodo(newSchedule, target);
        }
    }

}
