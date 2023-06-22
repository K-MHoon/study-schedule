package com.example.studyschedule.helper;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ScheduleHelper {

    @Autowired
    protected ScheduleRepository scheduleRepository;

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
