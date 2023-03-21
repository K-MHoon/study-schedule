package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.service.member.MemberCommonService;
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
    private final ScheduleCommonService scheduleCommonService;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getMemberScheduleList() {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        return scheduleRepository.findAllByMember_Id(loggedInMember.getId()).stream()
                .map(ScheduleDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto getSchedule(Long scheduleId) {
        Schedule schedule = scheduleCommonService.validateExistedScheduleId(scheduleId);
        return ScheduleDto.entityToDtoWithTodo(schedule);
    }

    @Transactional
    public Schedule createSchedule(ScheduleControllerRequest.CreateScheduleRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
        }

        Schedule newSchedule = new Schedule(loggedInMember, request.getStartDate(), request.getEndDate(), request.getIsUse(), request.getName());

        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        if(!request.getTodoList().isEmpty()) {
            createScheduleTodo(request.getTodoList(), loggedInMember, savedSchedule);
        }

        return savedSchedule;
    }

    private void createScheduleTodo(List<Long> targetIdList, Member member, Schedule newSchedule) {
        List<Todo> target = todoCommonService.getTodoListByIdList(targetIdList);
        List<Todo> todoListLinkedMember = todoCommonService.getTodoListLinkedMember(member);

        if(todoCommonService.checkTargetTodoListInNormalTodoList(target, todoListLinkedMember)) {
            scheduleTodoService.createScheduleTodo(newSchedule, target);
        }
    }

    @Transactional
    public void deleteScheduleAll(ScheduleControllerRequest.DeleteScheduleRequest request) {
        Member member = memberCommonService.getLoggedInMember();
        if(isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스케줄을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        scheduleRepository.deleteAllByIdInAndMember_Id(request.getScheduleList(), member.getId());

    }

    private boolean isNotSameRequestAndDataCount(ScheduleControllerRequest.DeleteScheduleRequest request, Member member) {
        return scheduleRepository.countAllByIdInAndMember_Id(request.getScheduleList(), member.getId()) != request.getScheduleList().size();
    }
}
