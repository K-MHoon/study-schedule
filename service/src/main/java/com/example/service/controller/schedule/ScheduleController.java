package com.example.service.controller.schedule;

import com.example.common.enums.ScheduleType;
import com.example.common.model.dto.schedule.ScheduleDto;
import com.example.service.controller.request.schedule.ScheduleControllerRequest;
import com.example.service.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDto> getMemberScheduleList(@RequestParam Long studyId) {
        return scheduleService.getMemberScheduleList(studyId);
    }

    @GetMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDto> getTodayScheduleList(@RequestParam(value = "type", required = false) ScheduleType scheduleType) {
        return scheduleService.getTodayScheduleList(scheduleType);
    }

    @PostMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public void updateTodayScheduleList(@RequestBody @Validated ScheduleControllerRequest.UpdateTodaySchedule request) {
        scheduleService.updateTodayScheduleList(request.toServiceRequest());
    }

    @GetMapping("/{schedule_id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDto getSchedule(@PathVariable(name = "schedule_id") Long scheduleId) {
        return scheduleService.getSchedule(scheduleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createSchedule(@RequestBody @Validated ScheduleControllerRequest.CreateSchedule request) {
        scheduleService.createSchedule(request.toServiceRequest());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteScheduleAll(@RequestBody @Validated ScheduleControllerRequest.DeleteSchedule request) {
        scheduleService.deleteScheduleAll(request.toServiceRequest());
    }
}
