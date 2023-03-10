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
    public List<ScheduleDto> getMemberScheduleList(Long memberId) {
        memberCommonService.validateExistedMemberById(memberId);
        return scheduleRepository.findAllByMember_Id(memberId).stream()
                .map(ScheduleDto::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto getSchedule(Long scheduleId) {
        Schedule schedule = scheduleCommonService.validateExistedScheduleId(scheduleId);
        return ScheduleDto.entityToDtoWithTodo(schedule);
    }

    @Transactional
    public Schedule createSchedule(Long memberId, ScheduleControllerRequest.CreateScheduleRequest request) {
        Member member = memberCommonService.validateExistedMemberById(memberId);
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("μμ μΌμκ° μ’λ£ μΌμλ³΄λ€ λ€μ μμ μ μμ΅λλ€.");
        }
        Schedule newSchedule = new Schedule(member, request.getStartDate(), request.getEndDate(), request.getIsUse(), request.getName());
        Schedule savedSchedule = scheduleRepository.save(newSchedule);
        if(!request.getTodoList().isEmpty()) {
            createScheduleTodo(request.getTodoList(), member, savedSchedule);
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
            throw new IllegalArgumentException("ν΄λΉ μ¬μ©μκ° μ­μ ν  μ μλ μ€μΌμ€μ ν¬ν¨νκ³  μμ΅λλ€. memberId = " + member.getMemberId());
        }
        scheduleRepository.deleteAllByIdInAndMember_Id(request.getScheduleList(), member.getId());

    }

    private boolean isNotSameRequestAndDataCount(ScheduleControllerRequest.DeleteScheduleRequest request, Member member) {
        return scheduleRepository.countAllByIdInAndMember_Id(request.getScheduleList(), member.getId()) != request.getScheduleList().size();
    }
}
