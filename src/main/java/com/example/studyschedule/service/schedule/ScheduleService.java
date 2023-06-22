package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsClear;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        Map<Schedule, List<ScheduleTodo>> groupingByScheduleToScheduleTodoListMap = scheduleTodoService.getScheduleTodoList(scheduleList)
                .stream()
                .collect(groupingBy(scheduleTodo -> scheduleTodo.getSchedule()));

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

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
        }

        Study study = getValidatedStudy(request, loggedInMember);

        Schedule newSchedule =  Schedule.builder()
                .member(loggedInMember)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isUse(request.getIsUse())
                .name(request.getName())
                .study(study)
                .build();

        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        if (!request.getTodoList().isEmpty()) {
            createScheduleTodo(request.getTodoList(), loggedInMember, savedSchedule);
        }

        return savedSchedule;
    }

    private Study getValidatedStudy(ScheduleControllerRequest.CreateScheduleRequest request, Member loggedInMember) {
        Study study = studyRepository.findById(request.getStudyId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디 입니다."));

        if(!studyMemberRepository.existsStudyMemberByStudy_IdAndMember_Id(request.getStudyId(), loggedInMember.getId())) {
            throw new IllegalArgumentException("스터디에 가입되지 않은 회원입니다.");
        }
        return study;
    }

    private void createScheduleTodo(List<Long> targetIdList, Member member, Schedule newSchedule) {
        List<Todo> target = todoCommonService.getTodoListByIdList(targetIdList);
        List<Todo> todoListLinkedMember = todoCommonService.getTodoListLinkedMember(member);

        if (todoCommonService.checkTargetTodoListInNormalTodoList(target, todoListLinkedMember)) {
            scheduleTodoService.createScheduleTodo(newSchedule, target);
        }
    }

    @Transactional
    public void deleteScheduleAll(ScheduleControllerRequest.DeleteScheduleRequest request) {
        Member member = memberCommonService.getLoggedInMember();
        if (isNotSameRequestAndDataCount(request, member)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스케줄을 포함하고 있습니다. memberId = " + member.getMemberId());
        }
        scheduleRepository.deleteAllByIdInAndMember_Id(request.getScheduleList(), member.getId());

    }

    private boolean isNotSameRequestAndDataCount(ScheduleControllerRequest.DeleteScheduleRequest request, Member member) {
        return scheduleRepository.countAllByIdInAndMember_Id(request.getScheduleList(), member.getId()) != request.getScheduleList().size();
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getTodayScheduleList() {
        List<Schedule> todayScheduleList = getMyTodayScheuleList();
        Map<Schedule, List<ScheduleTodo>> groupingByScheduleToScheduleTodoListMap = scheduleTodoService.getScheduleTodoList(todayScheduleList)
                .stream()
                .collect(groupingBy(scheduleTodo -> scheduleTodo.getSchedule()));

        return todayScheduleList.stream()
                .map(schedule -> {
                    ScheduleDto scheduleDto = ScheduleDto.entityToDto(schedule);
                    scheduleDto.updateStudy(schedule.getStudy());
                    scheduleDto.updateTodoList(groupingByScheduleToScheduleTodoListMap.getOrDefault(schedule, Collections.emptyList()));
                    return scheduleDto;
                })
                .collect(Collectors.toList());

    }

    private List<Schedule> getMyTodayScheuleList() {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<Study> studyList = studyMemberRepository.findAllMyStudy(loggedInMember.getId());
        return scheduleRepository.findAllTodayMySchedule(loggedInMember, studyList, LocalDateTime.now(), IsUse.Y);
    }

    @Transactional
    public void updateTodayScheduleList(ScheduleControllerRequest.UpdateTodayScheduleRequest request) {
        List<Schedule> todayScheduleList = getMyTodayScheuleList();

        Map<Long, List<ScheduleTodo>> scheduleIdToScheduleTodoMap = scheduleTodoService.getScheduleTodoList(todayScheduleList)
                .stream()
                .collect(groupingBy(scheduleTodo -> scheduleTodo.getSchedule().getId()));

        for (ScheduleControllerRequest.Element element : request.getClearScheduleTodoList()) {

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
