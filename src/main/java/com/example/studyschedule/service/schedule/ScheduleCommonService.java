package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleCommonService {

    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Schedule validateExistedScheduleId(Long scheduleId) {
        if(Objects.isNull(scheduleId)) {
            throw new IllegalArgumentException("스케줄 ID 정보는 null일 수 없습니다.");
        }
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ID에 해당하는 스케줄을 찾을 수 없습니다. id = %d", scheduleId)));
    }
}
