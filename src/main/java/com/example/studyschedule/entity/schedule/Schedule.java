package com.example.studyschedule.entity.schedule;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.SchedulePeriod;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private SchedulePeriod period;

    private Long custom; // period가 CUSTOM인 경우 사용

    @Builder
    public Schedule(Long id, String name, List<ScheduleTodo> scheduleTodoList, Study study, Member member, SchedulePeriod period, Long custom, LocalDateTime startDate, LocalDateTime endDate, IsUse isUse) {
        this.id = id;
        this.name = name;
        this.scheduleTodoList = scheduleTodoList;
        this.study = study;
        this.member = member;
        this.period = period == null ? SchedulePeriod.DAY : period;
        this.custom = custom;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isUse = isUse;
    }

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * 진행중인 스케줄인지 여부
     */
    @Enumerated(EnumType.STRING)
    private IsUse isUse;
}
