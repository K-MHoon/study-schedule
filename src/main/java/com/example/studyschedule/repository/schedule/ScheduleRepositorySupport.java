package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositorySupport {

    List<Schedule> findAllTodayMySchedule(Member member, List<Study> studyList, LocalDateTime checkDate, IsUse isUse);

}
