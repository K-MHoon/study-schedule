package com.example.studyschedule.entity.study;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_member_map")
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public StudyMember(Member member, Study study) {
        this.member = member;
        this.study = study;
    }
}
