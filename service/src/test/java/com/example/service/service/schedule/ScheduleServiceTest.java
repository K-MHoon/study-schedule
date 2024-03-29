package com.example.service.service.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.Todo;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.common.model.dto.schedule.ScheduleDto;
import com.example.common.repository.schedule.ScheduleRepository;
import com.example.service.TestHelper;
import com.example.service.service.schedule.request.ScheduleServiceRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
    @Tag("long term")
    @DisplayName("기간 타입의 스케줄을 정상적으로 생성된다.")
    void createScheduleByLongTermType() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = ScheduleServiceRequest.CreateSchedule.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .isUse(IsUse.Y)
                .scheduleType(ScheduleType.LONG_TERM)
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
                () -> assertThat(schedule.getScheduleType()).isEqualTo(ScheduleType.LONG_TERM),
                () -> assertThat(schedule.getName()).isEqualTo(request.getName()),
                () -> assertThat(schedule.getStudy().getId()).isEqualTo(simpleStudy.getId()));
    }

    @Test
    @Tag("pattern")
    @DisplayName("패턴 타입의 스케줄을 정상적으로 생성된다.")
    void createScheduleByPatternType() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = createScheduleRequestPatternBuilder(simpleStudy)
                .period(SchedulePeriod.DAY)
                .build();

        // when
        Schedule schedule = scheduleService.createSchedule(request);

        // then
        assertAll(() -> assertThat(schedule.getMember()).isEqualTo(member),
                () -> assertThat(schedule.getPeriod()).isEqualTo(SchedulePeriod.DAY),
                () -> assertThat(schedule.getIsUse()).isEqualTo(request.getIsUse()),
                () -> assertThat(schedule.getScheduleType()).isEqualTo(ScheduleType.PATTERN),
                () -> assertThat(schedule.getName()).isEqualTo(request.getName()),
                () -> assertThat(schedule.getStudy().getId()).isEqualTo(simpleStudy.getId()));
    }

    @ParameterizedTest
    @MethodSource("nextScheduleDate")
    @Tag("pattern")
    @DisplayName("SchedulePeriod 설정에 따라 다음 날짜가 지정된다.")
    void getNextScheduleDateBySchedulePeriodType(SchedulePeriod schedulePeriod, LocalDate nextDate) {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = createScheduleRequestPatternBuilder(simpleStudy)
                .period(schedulePeriod)
                .build();

        // when
        Schedule schedule = scheduleService.createSchedule(request);

        // then
        assertThat(schedule.getNextScheduleDate().isEqual(nextDate)).isTrue();
    }

    static Stream<Arguments> nextScheduleDate() {
        return Stream.of(arguments(SchedulePeriod.DAY, LocalDate.now().plusDays(1)),
                arguments(SchedulePeriod.WEEK, LocalDate.now().plusWeeks(1)),
                arguments(SchedulePeriod.MONTH, LocalDate.now().plusMonths(1)),
                arguments(SchedulePeriod.YEAR, LocalDate.now().plusYears(1)));
    }

    @Test
    @Tag("pattern")
    @DisplayName("SchedulePeriod가 CUSTOM 경우, 정해진 일자 만큼으로 다음 날짜가 지정된다.")
    void schedulePeriodIsCustom() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = createScheduleRequestPatternBuilder(simpleStudy)
                .period(SchedulePeriod.CUSTOM)
                .customDay(10L)
                .build();

        // when
        Schedule schedule = scheduleService.createSchedule(request);

        // then
        assertThat(schedule.getNextScheduleDate().isEqual(LocalDate.now().plusDays(10))).isTrue();
    }
    @Test
    @Tag("pattern")
    @DisplayName("SchedulePeriod를 WEEK로 설정하면 다음 주가 예약일로 지정된다.")
    void schedulePeriodIsWEEK() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = createScheduleRequestPatternBuilder(simpleStudy)
                .period(SchedulePeriod.WEEK)
                .build();

        // when
        Schedule schedule = scheduleService.createSchedule(request);

        // then
        assertThat(schedule.getNextScheduleDate()).isEqualTo(LocalDate.now().plusWeeks(1));
    }



    private ScheduleServiceRequest.CreateSchedule.CreateScheduleBuilder createScheduleRequestPatternBuilder(Study simpleStudy) {
        return ScheduleServiceRequest.CreateSchedule.builder()
                .isUse(IsUse.Y)
                .scheduleType(ScheduleType.PATTERN)
                .name("새로운 스케줄")
                .studyId(simpleStudy.getId())
                .todoList(Collections.emptyList());
    }

    @Test
    @DisplayName("시작 일자가 종료 일자보다 큰 경우 예외가 발생한다.")
    void rejectCreateScheduleWhenStartDateMoreThanEndDate() {
        // given
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        ScheduleServiceRequest.CreateSchedule request = ScheduleServiceRequest.CreateSchedule.builder()
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now())
                .isUse(IsUse.Y)
                .scheduleType(ScheduleType.LONG_TERM)
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
        Study simpleStudy = studyHelper.createStudyWithStudyMember(member);
        List<Todo> todoList = todoHelper.createTestTodosAndSaveByCount(member, 3);
        List<Long> todoIdList = todoList.stream().map(Todo::getId).collect(Collectors.toList());
        ScheduleServiceRequest.CreateSchedule request = ScheduleServiceRequest.CreateSchedule.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .isUse(IsUse.Y)
                .scheduleType(ScheduleType.LONG_TERM)
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
    @DisplayName("요청된 스케줄 삭제하면, 전체가 미사용 처리로 변경된다.")
    void deleteScheduleAllSuccess() {
        // given
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 10);
        ScheduleServiceRequest.DeleteSchedule request = ScheduleServiceRequest.DeleteSchedule
                .builder()
                .scheduleList(scheduleList.stream().map(Schedule::getId).collect(Collectors.toList()))
                .build();

        // when
        scheduleService.deleteScheduleAll(request);

        // then
        List<Schedule> allScheduleList = scheduleRepository.findAll();
        assertThat(allScheduleList).hasSize(10)
                .extracting("isUse")
                .containsOnly(IsUse.N);
    }

    @Test
    @DisplayName("로그인된 멤버에 해당하지 않는 스케줄 id를 요청할 경우 예외가 발생한다.")
    void causeExceptionWhenNotLoggedInMemberScheduleDeleteRequest() {
        // given
        Member member2 = memberHelper.createSimpleMember("anotherMember");
        List<Schedule> scheduleList = scheduleHelper.createTestSchedulesAndSaveByCount(member, 10);
        scheduleList.addAll(scheduleHelper.createTestSchedulesAndSaveByCount(member2, 2));
        ScheduleServiceRequest.DeleteSchedule request = ScheduleServiceRequest.DeleteSchedule
                .builder()
                .scheduleList(scheduleList.stream().map(Schedule::getId).collect(Collectors.toList()))
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleService.deleteScheduleAll(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자가 삭제할 수 없는 스케줄을 포함하고 있습니다. memberId = " + member.getMemberId());
    }
}