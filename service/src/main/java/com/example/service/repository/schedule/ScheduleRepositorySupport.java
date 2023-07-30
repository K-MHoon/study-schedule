package com.example.service.repository.schedule;

import com.example.service.entity.member.Member;
import com.example.service.entity.schedule.Schedule;
import com.example.service.entity.study.Study;
import com.example.service.enums.IsUse;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositorySupport {

    List<Schedule> findAllTodayMySchedule(Member member, List<Study> studyList, LocalDateTime checkDate, IsUse isUse);

}
