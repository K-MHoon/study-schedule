package com.example.common.repository.schedule;

import com.example.common.entity.schedule.ScheduleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleHistoryRepository extends JpaRepository<ScheduleHistory, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ScheduleHistory sh where sh.schedule.id in (:scheduleIdList)")
    int deleteAllByScheduleIdList(@Param("scheduleIdList") List<Long> scheduleIdList);
}
