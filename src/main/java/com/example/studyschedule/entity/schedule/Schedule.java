package com.example.studyschedule.entity.schedule;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.enums.IsUse;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleTodo> scheduleTodoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Member member;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * 진행중인 스케줄인지 여부
     */
    @Enumerated(EnumType.STRING)
    private IsUse isUse;
}
