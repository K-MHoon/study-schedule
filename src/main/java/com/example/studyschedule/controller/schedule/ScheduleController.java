package com.example.studyschedule.controller.schedule;

import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{member_id}")
    public ResponseEntity<List<ScheduleDto>> getMemberScheduleList(@PathVariable(name = "member_id") Long memberId) {
        log.info("[getMemberSchedule] call");

        List<ScheduleDto> response = scheduleService.getMemberScheduleList(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
