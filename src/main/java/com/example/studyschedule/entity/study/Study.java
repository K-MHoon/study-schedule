package com.example.studyschedule.entity.study;

import com.example.studyschedule.entity.common.BaseEntity;
import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.enums.IsUse;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StudyMember> studyMemberList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader; // 스터디 방장

    private String name; // 스터디 이름

    private Boolean secret; // 비공개 여부

    private String password; // 비밀번호

    private Long fullCount; // 스터디 최대 인원

    @Enumerated(EnumType.STRING)
    private IsUse isUse; // 스터디 사용 여부

    public static Study ofPublic(Member leader, String name, Long fullCount, IsUse isUse) {
        return Study.builder()
                .leader(leader)
                .name(name)
                .secret(false)
                .fullCount(fullCount)
                .isUse(isUse)
                .build();
    }

    public void changeToPrivate(String password) {
        if(StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("사설 스터디에는 반드시 비밀번호가 포함되어야 합니다.");
        }
        this.secret = true;
        this.password = password;
    }

    public void addStudyMember(Member member) {
        this.studyMemberList.add(new StudyMember(member, this));
    }

    public Long getRemainCount() {
        return Long.valueOf(this.studyMemberList.size());
    }

    public String getLeaderName() {
        return this.leader.getName();
    }
}
