package com.example.studyschedule.service.schedule;

import com.example.studyschedule.TestHelper;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScheduleServiceTest extends TestHelper {

    @Autowired
    ScheduleService scheduleService;

    @Test
    @DisplayName("회원 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    void getMemberScheduleList() {
        List<Member> memberList = createTestMembersAndSaveByCount(2);
        List<Schedule> scheduleList = createTestSchedulesAndSaveByCount(memberList.get(0), 2);

        List<ScheduleDto> result = scheduleService.getMemberScheduleList(memberList.get(0).getId());

        assertAll(() -> assertThat(result).hasSize(2),
                () -> assertThat(result).extracting("id").containsExactlyInAnyOrder(scheduleList.get(0).getId(), scheduleList.get(1).getId()),
                () -> assertThat(result).extracting("memberId").containsOnly(memberList.get(0).getId()));

    }

    @Test
    @DisplayName("스케줄 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    void getSchedule() {
        Member member = createSimpleMember();
        List<Schedule> scheduleList = createTestSchedulesAndSaveByCount(member, 2);

        ScheduleDto result = scheduleService.getSchedule(scheduleList.get(0).getId());

        assertThat(result.getId()).isEqualTo(scheduleList.get(0).getId());
    }

    @Test
    @DisplayName("스케줄 id가 존재하지 않는 경우 예외가 발생한다.")
    void causeExceptionWhenHasNotScheduleId() {
        assertThatThrownBy(() -> scheduleService.getSchedule(1_000_000L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("message")
                .isEqualTo("ID에 해당하는 스케줄을 찾을 수 없습니다. id = 1000000");
    }

    @Test
    @DisplayName("스케줄이 정상적으로 생성된다.")
    void createSchedule() {
        // given
        Member member = createSimpleMember();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(10);
        IsUse isUse = IsUse.Y;
        String name = "새로운 스케줄";

        ScheduleControllerRequest.CreateScheduleRequest request = new ScheduleControllerRequest.CreateScheduleRequest(name, startDate, endDate, isUse, Collections.emptyList());

        // when
        Schedule schedule = scheduleService.createSchedule(member.getId(), request);

        // then
        assertAll(() -> assertThat(schedule.getMember()).isEqualTo(member),
                () -> assertThat(schedule.getStartDate()).isEqualTo(startDate),
                () -> assertThat(schedule.getEndDate()).isEqualTo(endDate),
                () -> assertThat(schedule.getIsUse()).isEqualTo(isUse),
                () -> assertThat(schedule.getName()).isEqualTo(name));
    }
}