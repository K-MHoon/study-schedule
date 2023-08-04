package com.example.common.repository.schedule;

import com.example.common.entity.member.Member;
import com.example.common.entity.member.QMember;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.util.List;

import static com.example.common.entity.schedule.QSchedule.schedule;
import static com.example.common.entity.study.QStudy.study;

public class ScheduleRepositorySupportImpl extends QuerydslRepositorySupport implements ScheduleRepositorySupport {
    public ScheduleRepositorySupportImpl() {
        super(Schedule.class);
    }

    @Override
    public List<Schedule> findAllTodayMySchedule(Member member, List<Study> studyList, LocalDate checkDate, IsUse isUse) {
        return from(schedule)
                .select(schedule)
                .leftJoin(schedule.study, study)
                .fetchJoin()
                .leftJoin(schedule.member, QMember.member)
                .fetchJoin()
                .where(schedule.member.eq(member),
                        schedule.study.in(studyList),
                        schedule.startDate.before(checkDate).and(schedule.endDate.after(checkDate)),
                        schedule.isUse.eq(isUse))
                .fetch();
    }
}
