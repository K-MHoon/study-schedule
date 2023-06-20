package com.example.studyschedule.service.schedule;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScheduleServiceTest extends TestHelper {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("특정 스터디에 연결된 회원의 스케줄 정보를 가지고 온다.")
    void getMemberScheduleList() {
        Study testStudy = studyHelper.createSimpleStudy(member);
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 2, testStudy);

        List<ScheduleDto> result = scheduleService.getMemberScheduleList(testStudy.getId());

        assertAll(() -> assertThat(result).hasSize(2),
                () -> assertThat(result).extracting("id").containsExactlyInAnyOrder(scheduleList.get(0).getId(), scheduleList.get(1).getId()),
                () -> assertThat(result).extracting("memberId").containsOnly(member.getId()));

    }

    @Test
    @DisplayName("스케줄 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    void getSchedule() {
        Study study = studyHelper.createSimpleStudy(member);
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 2, study);

        ScheduleDto result = scheduleService.getSchedule(scheduleList.get(0).getId());

        assertThat(result.getId()).isEqualTo(scheduleList.get(0).getId());
    }

    @Test
    @DisplayName("스케줄 id가 존재하지 않는 경우 예외가 발생한다.")
    void causeExceptionWhenHasNotScheduleId() {
        assertThatThrownBy(() -> scheduleService.getSchedule(Long.MAX_VALUE))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("message")
                .isEqualTo("ID에 해당하는 스케줄을 찾을 수 없습니다. id = " + Long.MAX_VALUE);
    }

    @Test
    @DisplayName("스케줄이 정상적으로 생성된다.")
    void createSchedule() {
        // given
        Study simpleStudy = studyHelper.createMemberWithStudyMember(member);
        ScheduleControllerRequest.CreateScheduleRequest request = ScheduleControllerRequest.CreateScheduleRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .isUse(IsUse.Y)
                .name("새로운 스케줄")
                .studyId(simpleStudy.getId())
                .todoList(Collections.emptyList())
                .build();

        // when
        Schedule schedule = scheduleService.createSchedule(request);

        // then
        assertAll(() -> assertThat(schedule.getMember()).isEqualTo(member),
                () -> assertThat(schedule.getStartDate()).isEqualTo(request.getStartDate()),
                () -> assertThat(schedule.getEndDate()).isEqualTo(request.getEndDate()),
                () -> assertThat(schedule.getIsUse()).isEqualTo(request.getIsUse()),
                () -> assertThat(schedule.getName()).isEqualTo(request.getName()),
                () -> assertThat(schedule.getStudy().getId()).isEqualTo(simpleStudy.getId()));
    }

    @Test
    @DisplayName("시작 일자가 종료 일자보다 큰 경우 예외가 발생한다.")
    void rejectcreateScheduleWhenStartDateMoreThanEndDate() {
        // given
        Study simpleStudy = studyHelper.createMemberWithStudyMember(member);
        ScheduleControllerRequest.CreateScheduleRequest request = ScheduleControllerRequest.CreateScheduleRequest.builder()
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now())
                .isUse(IsUse.Y)
                .name("새로운 스케줄")
                .studyId(simpleStudy.getId())
                .todoList(Collections.emptyList())
                .build();

        // when & then
        assertThatThrownBy(() ->scheduleService.createSchedule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 일자가 종료 일자보다 뒤에 있을 수 없습니다.");
    }

    @Test
    @DisplayName("스케줄을 생성할 때 연결한 할 일이 추가 된다.")
    void createScheduleWithTodoList() {
        // given
        Study simpleStudy = studyHelper.createMemberWithStudyMember(member);
        List<Todo> todoList = todoHelper.createTestTodosAndSaveByCount(member, 3);
        List<Long> todoIdList = todoList.stream().map(Todo::getId).collect(Collectors.toList());
        ScheduleControllerRequest.CreateScheduleRequest request = ScheduleControllerRequest.CreateScheduleRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .isUse(IsUse.Y)
                .name("새로운 스케줄")
                .studyId(simpleStudy.getId())
                .todoList(todoIdList)
                .build();

        // when
        scheduleService.createSchedule(request);

        // then
        entityManagerFlushAndClear();
        List<Schedule> result = scheduleRepository.findAll();
        assertAll(() -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getScheduleTodoList()).hasSize(3),
                () -> assertThat(result.get(0).getScheduleTodoList())
                        .extracting("todo")
                        .extracting("id")
                        .containsAnyElementsOf(todoIdList));
    }

    @Test
    @DisplayName("요청된 스케줄 전체를 정상적으로 삭제한다.")
    void deleteScheduleAllSuccess() {
        // given
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 10);
        ScheduleControllerRequest.DeleteScheduleRequest request = new ScheduleControllerRequest.DeleteScheduleRequest(scheduleList.stream().map(Schedule::getId).collect(Collectors.toList()));

        // when
        scheduleService.deleteScheduleAll(request);

        // then
        List<Schedule> allScheduleList = scheduleRepository.findAll();
        assertThat(allScheduleList).hasSize(0);
    }

    @Test
    @DisplayName("로그인된 멤버에 해당하지 않는 스케줄 id를 요청할 경우 예외가 발생한다.")
    void causeExceptionWhenNotLoggedInMemberScheduleDeleteRequest() {
        // given
        Member member2 = memberHelper.createSimpleMember("anotherMember");
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 10);
        scheduleList.addAll(scheduleHelper.createTestSchedulesAndSaveByCount(member2, 2));
        ScheduleControllerRequest.DeleteScheduleRequest request = new ScheduleControllerRequest.DeleteScheduleRequest(scheduleList.stream().map(Schedule::getId).collect(Collectors.toList()));

        // when & then
        assertThatThrownBy(() -> scheduleService.deleteScheduleAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 스케줄을 포함하고 있습니다. memberId = " + member.getMemberId());
    }
}