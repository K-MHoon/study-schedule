package com.example.studyschedule.service.schedule;

import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @Test
    @DisplayName("회원 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    void getMemberScheduleList() {
        Long memberId = 1L;

        List<ScheduleDto> result = scheduleService.getMemberScheduleList(memberId);
        Long target = result.stream().map(ScheduleDto::getMemberId).findAny().get();

        System.out.println(result);
        assertEquals(memberId, target);
    }

    @Test
    @DisplayName("스케줄 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    void getSchedule() {
        Long scheduleId = 1L;

        ScheduleDto result = scheduleService.getSchedule(scheduleId);

        assertEquals(scheduleId, result.getId());
    }

    @Test
    @DisplayName("스케줄 id가 존재하지 않는 경우 예외가 발생한다.")
    void causeExceptionWhenHasNotScheduleId() {
        Long scheduleId = 1_000_000L;

        assertThrows(EntityNotFoundException.class, () -> scheduleService.getSchedule(scheduleId));
    }
}