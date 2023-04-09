package com.example.studyschedule.entity.study;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.enums.RegisterState;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 스터디 가입 요청 상태 정보를 담고 있는 엔티티
 */
@Entity
@Table(name = "study_register")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRegister extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_study_id")
    private Study requestStudy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_member_id")
    private Member requestMember;

    private String goal; // 가입 목적

    private String objective; // 가입 목표

    private String comment; // 추가 메시지

    @Enumerated(EnumType.STRING)
    private RegisterState state; // 현재 진행 상태

    private LocalDateTime approvalAt; // 승인 또는 거절 일자

    @OneToOne(fetch = FetchType.LAZY)
    private Member approvalBy; // 승인 또는 거절한 사람

    @Builder
    public StudyRegister(Study requestStudy, Member requestMember, String goal, String objective, String comment, RegisterState state, LocalDateTime approvalAt, Member approvalBy) {
        this.requestStudy = requestStudy;
        this.requestMember = requestMember;
        this.goal = goal;
        this.objective = objective;
        this.comment = comment;
        this.state = state;
        this.approvalAt = approvalAt;
        this.approvalBy = approvalBy;
    }
}
