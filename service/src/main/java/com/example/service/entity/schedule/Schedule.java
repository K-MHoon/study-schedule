package com.example.service.entity.schedule;

import com.example.service.entity.common.BaseEntity;
import com.example.service.entity.member.Member;
import com.example.service.entity.study.Study;
import com.example.service.enums.IsUse;
import com.example.service.enums.SchedulePeriod;
import com.example.service.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_schedule")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleTodo> scheduleTodoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    // Pattern Schedule
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'DAY'")
    private SchedulePeriod period;

    private Long custom; // period가 CUSTOM인 경우 사용

    private LocalDateTime nextScheduleDate;

    // Long Schedule
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * 진행중인 스케줄인지 여부
     */
    @Enumerated(EnumType.STRING)
    private IsUse isUse;

    public void updateNextScheduleDate(LocalDateTime nextScheduleDate) {
        this.nextScheduleDate = nextScheduleDate;
    }
}
