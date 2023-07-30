package com.example.service.entity.schedule;

import com.example.service.enums.IsClear;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_history")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScheduleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime activeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    /**
     * 특정 사유로 하지 못한 내용 작성
     */
    @Column(columnDefinition = "TEXT")
    private String reason;

    /**
     * 해당 스케줄의 할 일이 완료됐는지 여부
     */
    @Enumerated(value = EnumType.STRING)
    private IsClear isClear;

    public void updateIsClear(IsClear isClear) {
        this.isClear = isClear;
    }

    public void updateReason(String reason) {
        this.reason = reason;
    }
}
