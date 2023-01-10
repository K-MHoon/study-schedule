package com.example.studyschedule.service.schedule;

import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @Test
    @DisplayName("회원 id에 해당하는 스케줄 정보를 정상적으로 가져온다.")
    @Transactional
    @Rollback
    public void getMemberScheduleList() {
        Long memberId = 1L;

        List<ScheduleDto> result = scheduleService.getMemberScheduleList(memberId);
        Long target = result.stream().map(ScheduleDto::getMemberId).findAny().get();

        System.out.println(result);
        assertEquals(memberId, target);
    }
}