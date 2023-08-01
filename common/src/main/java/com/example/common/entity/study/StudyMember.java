package com.example.common.entity.study;

import com.example.common.entity.common.BaseEntity;
import com.example.common.entity.member.Member;
import com.example.common.entity.common.BaseEntity;
import com.example.common.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_member_map")
@Getter
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    @NotNull
    private Study study;

    public StudyMember(Member member, Study study) {
        this.member = member;
        this.study = study;
    }
}
