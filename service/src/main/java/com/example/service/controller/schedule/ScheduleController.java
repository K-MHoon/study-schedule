package com.example.service.controller.schedule;

import com.example.common.model.dto.schedule.ScheduleDto;
import com.example.common.model.request.schedule.ScheduleControllerRequest;
import com.example.service.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDto> getMemberScheduleList(Principal principal, @RequestParam Long studyId) {
        log.info("[getMemberSchedule] called by {}, studyId = {}", principal.getName(), studyId);

        return scheduleService.getMemberScheduleList(studyId);
    }

    @GetMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDto> getTodayScheduleList(Principal principal) {
        log.info("[getTodayScheduleList] called by {}", principal.getName());

        return scheduleService.getTodayScheduleList();
    }

    @PostMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public void updateTodayScheduleList(Principal principal, @RequestBody @Validated ScheduleControllerRequest.UpdateTodayScheduleRequest request) {
        log.info("[updateTodayScheduleList] called by {}", principal.getName());

        scheduleService.updateTodayScheduleList(request);
    }

    @GetMapping("/{schedule_id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDto getSchedule(Principal principal, @PathVariable(name = "schedule_id") Long scheduleId) {
        log.info("[getSchedule] called by {}, schedule Id = {}", principal.getName(), scheduleId);

        return scheduleService.getSchedule(scheduleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createSchedule(Principal principal, @RequestBody @Validated ScheduleControllerRequest.CreateScheduleRequest request) {
        log.info("[createSchedule] called by {}, body = {}", principal.getName(), request);

        scheduleService.createSchedule(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteScheduleAll(@RequestBody @Validated ScheduleControllerRequest.DeleteScheduleRequest request,
                                  Principal principal) {
        log.info("[deleteScheduleAll] called by memberId = {}, scheduleList = {}", principal.getName(), request.getScheduleList());

        scheduleService.deleteScheduleAll(request);
    }
}