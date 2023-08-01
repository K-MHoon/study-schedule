package com.example.common.entity.schedule;

import com.example.common.enums.IsClear;
import com.example.common.entity.common.BaseEntity;
import com.example.common.enums.IsClear;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_todo_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleTodo extends BaseEntity {

    public ScheduleTodo(Schedule schedule, Todo todo) {
        this.schedule = schedule;
        this.todo = todo;
        this.isClear = IsClear.N;
    }

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

    public void updateIsClear(IsClear isClear) {
        this.isClear = isClear;
    }
}
