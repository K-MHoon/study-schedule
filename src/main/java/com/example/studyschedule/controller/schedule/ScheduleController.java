package com.example.studyschedule.controller.schedule;

import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.request.schedule.ScheduleControllerRequest;
import com.example.studyschedule.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/member/{member_id}")
    public ResponseEntity<List<ScheduleDto>> getMemberScheduleList(@PathVariable(name = "member_id") Long memberId) {
        log.info("[getMemberSchedule] call, memberId = {}", memberId);

        List<ScheduleDto> response = scheduleService.getMemberScheduleList(memberId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{schedule_id}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable(name = "schedule_id") Long scheduleId) {
        log.info("[getSchedule] call, scheduleId = {}", scheduleId);

        ScheduleDto response = scheduleService.getSchedule(scheduleId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/member/{member_id}")
    public ResponseEntity createSchedule(@PathVariable(name = "member_id") Long memberId,
                                         @RequestBody @Validated ScheduleControllerRequest.CreateScheduleRequest request) {
        log.info("[createSchedule] call, memberId = {}", memberId);

        scheduleService.createSchedule(memberId, request);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteScheduleAll(@RequestBody @Validated ScheduleControllerRequest.DeleteScheduleRequest request,
                                  Principal principal) {
        log.info("[deleteScheduleAll] called by memberId = {}, schedule List = {}", principal.getName(), request.getScheduleList());

        scheduleService.deleteScheduleAll(request);
    }
}
