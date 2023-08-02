package com.example.service.helper;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;
import com.example.common.repository.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Transactional
public class ScheduleHelper {

    @Autowired
    protected ScheduleRepository scheduleRepository;

    public Schedule createSimpleSchedule(Member member) {
        Schedule schedule = Schedule.builder()
                        .member(member)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusDays(10))
                        .isUse(IsUse.Y)
                        .name("simpleSchedule")
                        .build();
        return scheduleRepository.save(schedule);
    }

    public Optional<Schedule> findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    public List<Schedule> createTestSchedulesAndSaveByCount(Member member, int count) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Schedule schedule =
                            Schedule.builder()
                                    .member(member)
                                    .startDate(LocalDateTime.now())
                                    .endDate(LocalDateTime.now().plusDays(10))
                                    .isUse(IsUse.Y)
                                    .name("testSchedule" + c)
                                    .build();
                    return scheduleRepository.save(schedule);
                })
                .collect(Collectors.toList());
    }

    public List<Schedule> createTestSchedulesAndSaveByCount(Member member, int count, Study study) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Schedule schedule =
                            Schedule.builder()
                                    .member(member)
                                    .startDate(LocalDateTime.now())
                                    .endDate(LocalDateTime.now().plusDays(10))
                                    .isUse(IsUse.Y)
                                    .name("testSchedule" + c)
                                    .study(study)
                                    .build();
                    return scheduleRepository.save(schedule);
                })
                .collect(Collectors.toList());
    }

}
