package com.example.studyschedule.entity.schedule;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.enums.IsClear;
import jakarta.persistence.*;

@Entity
@Table(name = "schedule_todo_map")
public class ScheduleTodo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    /**
     * 해당 스케줄의 할 일이 완료됐는지 여부
     */
    @Enumerated(value = EnumType.STRING)
    private IsClear isClear;

    /**
     * 할 일을 모두 하지 못했다면 이유 작성
     */
    private String reason;
}
