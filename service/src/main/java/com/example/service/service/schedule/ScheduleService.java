package com.example.service.service.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.ScheduleTodo;
import com.example.common.entity.schedule.Todo;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsClear;
import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.common.model.dto.schedule.ScheduleDto;
import com.example.service.controller.request.schedule.ScheduleControllerRequest;
import com.example.common.repository.schedule.ScheduleRepository;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.common.repository.study.StudyRepository;
import com.example.service.service.member.MemberCommonService;
import com.example.service.service.schedule.request.ScheduleServiceRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberCommonService memberCommonService;
    private final TodoCommonService todoCommonService;
    private final ScheduleTodoService scheduleTodoService;
    private final ScheduleCommonService scheduleCommonService;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getMemberScheduleList(Long studyId) {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        Study study = studyRepository.findByIdAndIsUse(studyId, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 스터디를 찾을 수 없습니다."));

        List<Schedule> scheduleList = scheduleRepository.findAllByMemberAndStudy(loggedInMember, study);

        Map<Schedule, List<ScheduleTodo>> groupingByScheduleToScheduleTodoListMap = getScheduleToScheduleTodoListMap(scheduleList);

        return scheduleList.stream()
                .map(schedule -> {
                    ScheduleDto scheduleDto = ScheduleDto.entityToDto(schedule);
                    scheduleDto.updateTodoList(groupingByScheduleToScheduleTodoListMap.getOrDefault(schedule, Collections.emptyList()));
                    return scheduleDto;
                })
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
        Study study = getValidatedStudy(request, loggedInMember);

        Schedule newSchedule = null;
        if(request.getScheduleType() == ScheduleType.LONG_TERM) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
            }

            newSchedule = Schedule.builder()
                    .id(request.getScheduleId())
                    .member(loggedInMember)
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .isUse(request.getIsUse())
                    .name(request.getName())
                    .scheduleType(request.getScheduleType())
                    .study(study)
                    .build();
        } else {
            newSchedule = Schedule.builder()
                    .id(request.getScheduleId())
                    .member(loggedInMember)
                    .isUse(request.getIsUse())
                    .name(request.getName())
                    .study(study)
                    .scheduleType(request.getScheduleType())
                    .period(request.getPeriod())
                    .custom(request.getCustomDay())
                    .build();

            setNextScheduleDate(request, newSchedule);
        }

        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        if (!request.getTodoList().isEmpty()) {
            createScheduleTodo(request.getTodoList(), loggedInMember, savedSchedule);
        }

        return savedSchedule;
    }

    private void setNextScheduleDate(ScheduleControllerRequest.CreateScheduleRequest request, Schedule newSchedule) {
        if(request.getPeriod() == SchedulePeriod.DAY) {
            newSchedule.updateNextScheduleDate(LocalDate.now().plusDays(1));
        } else if(request.getPeriod() == SchedulePeriod.WEEK) {
            newSchedule.updateNextScheduleDate(LocalDate.now().plusWeeks(1));
        } else if(request.getPeriod() == SchedulePeriod.MONTH) {
            newSchedule.updateNextScheduleDate(LocalDate.now().plusMonths(1));
        } else if(request.getPeriod() == SchedulePeriod.YEAR) {
            newSchedule.updateNextScheduleDate(LocalDate.now().plusYears(1));
        } else {
            newSchedule.updateNextScheduleDate(LocalDate.now().plusDays(request.getCustomDay()));
        }
    }

    private Study getValidatedStudy(ScheduleControllerRequest.CreateScheduleRequest request, Member loggedInMember) {
        Study study = studyRepository.findById(request.getStudyId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디 입니다."));

        if (!studyMemberRepository.existsStudyMemberByStudy_IdAndMember_Id(request.getStudyId(), loggedInMember.getId())) {
            throw new IllegalArgumentException("스터디에 가입되지 않은 회원입니다.");
        }
        return study;
    }

    private void createScheduleTodo(List<Long> targetIdList, Member member, Schedule newSchedule) {
        List<Todo> target = todoCommonService.getTodoListByIdList(targetIdList);
        System.out.println(target.get(0).getId());
        List<Todo> todoListLinkedMember = todoCommonService.getTodoListLinkedMember(member);

        if (todoCommonService.checkTargetTodoListInNormalTodoList(target, todoListLinkedMember)) {

            List<ScheduleTodo> scheduleTodoList = scheduleTodoService.getScheduleTodoList(List.of(newSchedule));
            removeScheduleTodo(target, scheduleTodoList);

            List<Todo> createTargetTodoList = getFilteredNewTodoList(target, scheduleTodoList);

            scheduleTodoService.createScheduleTodo(newSchedule, createTargetTodoList);
        }
    }

    private List<Todo> getFilteredNewTodoList(List<Todo> target, List<ScheduleTodo> scheduleTodoList) {
        List<Todo> savedTodoList = scheduleTodoList.stream().map(ScheduleTodo::getTodo).collect(toList());
        List<Todo> createTargetTodoList = target.stream().filter(todo -> !savedTodoList.contains(todo)).collect(toList());
        return createTargetTodoList;
    }

    private void removeScheduleTodo(List<Todo> target, List<ScheduleTodo> scheduleTodoList) {
        List<Long> targetIdList = target.stream().map(Todo::getId).collect(toList());

        List<ScheduleTodo> removeTargetScheduleTodoList = scheduleTodoList.stream().filter(scheduleTodo -> !targetIdList.contains(scheduleTodo.getTodo().getId())).collect(toList());
        if(!removeTargetScheduleTodoList.isEmpty()) {
            scheduleTodoService.removeAllScheduleTodo(removeTargetScheduleTodoList);
        }
    }

    @Transactional
    public void deleteScheduleAll(ScheduleControllerRequest.DeleteScheduleRequest request) {
        Member member = memberCommonService.getLoggedInMember();
        if (isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스케줄을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        scheduleRepository.updateAllByScheduleIdInAndMember_Id(IsUse.N, request.getScheduleList(), member.getId());
    }

    private boolean isNotSameRequestAndDataCount(ScheduleControllerRequest.DeleteScheduleRequest request, Member member) {
        return scheduleRepository.countAllByIdInAndMember_Id(request.getScheduleList(), member.getId()) != request.getScheduleList().size();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getTodayScheduleList(ScheduleType scheduleType) {
        List<Schedule> todayScheduleList = getMyTodayScheuleList(scheduleType);
        Map<Schedule, List<ScheduleTodo>> groupingByScheduleToScheduleTodoListMap = getScheduleToScheduleTodoListMap(todayScheduleList);

        return todayScheduleList.stream()
                .map(schedule -> {
                    ScheduleDto scheduleDto = ScheduleDto.entityToDto(schedule);
                    scheduleDto.updateTodoList(groupingByScheduleToScheduleTodoListMap.getOrDefault(schedule, Collections.emptyList()));
                    return scheduleDto;
                })
                .collect(Collectors.toList());

    }

    @NotNull
    private Map<Schedule, List<ScheduleTodo>> getScheduleToScheduleTodoListMap(List<Schedule> todayScheduleList) {
        return scheduleTodoService.getScheduleTodoList(todayScheduleList)
                .stream()
                .collect(groupingBy(scheduleTodo -> scheduleTodo.getSchedule()));
    }

    private List<Schedule> getMyTodayScheuleList(ScheduleType scheduleType) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<Study> studyList = studyMemberRepository.findAllMyStudy(loggedInMember.getId());
        return scheduleRepository.findAllTodayMySchedule(loggedInMember, studyList, LocalDate.now(), IsUse.Y, scheduleType);
    }

    @Transactional
    public void updateTodayScheduleList(ScheduleServiceRequest.UpdateTodaySchedule request) {
        List<Schedule> todayScheduleList = getMyTodayScheuleList(ScheduleType.NONE);

        Map<Long, List<ScheduleTodo>> scheduleIdToScheduleTodoMap = scheduleTodoService.getScheduleTodoList(todayScheduleList)
                .stream()
                .collect(groupingBy(scheduleTodo -> scheduleTodo.getSchedule().getId()));

        for (ScheduleServiceRequest.Element element : request.getClearScheduleTodoList()) {

            if (!scheduleIdToScheduleTodoMap.containsKey(element.getScheduleId())) {
                throw new IllegalArgumentException("오늘 할 일에 스케줄이 존재하지 않습니다.");
            }

            Set<Long> clearTodoSet = element.getClearTodoIdList().stream().collect(toSet());
            for (ScheduleTodo scheduleTodo : scheduleIdToScheduleTodoMap.get(element.getScheduleId())) {
                if (clearTodoSet.contains(scheduleTodo.getTodo().getId())) {
                    scheduleTodo.updateIsClear(IsClear.Y);
                } else {
                    scheduleTodo.updateIsClear(IsClear.N);
                }
            }
        }
    }
}
