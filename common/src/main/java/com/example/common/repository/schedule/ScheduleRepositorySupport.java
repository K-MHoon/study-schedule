package com.example.common.repository.schedule;

import com.example.common.enums.IsUse;
import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositorySupport {

    List<Schedule> findAllTodayMySchedule(Member member, List<Study> studyList, LocalDateTime checkDate, IsUse isUse);

}
