package com.example.studyschedule.entity.study;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "study_code")
@NoArgsConstructor
@Getter
public final class StudyCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member useMember; // 코드를 사용한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    private String inviteCode;

    public StudyCode(Study study) {
        this.study = study;
        this.inviteCode = UUID.randomUUID().toString();
    }

    public void updateUseMember(Member member) {
        this.useMember = member;
    }
}
