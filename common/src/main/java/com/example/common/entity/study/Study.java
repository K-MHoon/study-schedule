package com.example.common.entity.study;

import com.example.common.entity.common.BaseEntity;
import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.enums.IsUse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyMember> studyMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "requestStudy", cascade = CascadeType.ALL)
    private List<StudyRegister> studyRegisterList = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader; // 스터디 방장

    @Column(nullable = false)
    private String name; // 스터디 이름

    @Column(nullable = false)
    private String content; // 스터디 소개

    @Column(nullable = false)
    private Boolean secret; // 비공개 여부

    private String password; // 비밀번호 (비밀 스터디 <> 공개 스터디 전환에 사용)

    @Column(nullable = false)
    private Long fullCount; // 스터디 최대 인원

    @OneToMany(mappedBy = "study")
    private List<StudyCode> studyCodeList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsUse isUse; // 스터디 사용 여부

    public static Study ofPublic(Member leader, String name, String content, Long fullCount, IsUse isUse) {
        return Study.builder()
                .leader(leader)
                .name(name)
                .content(content)
                .secret(false)
                .fullCount(fullCount)
                .isUse(isUse)
                .build();
    }

    @Builder
    private Study(Long id, List<StudyMember> studyMemberList, List<StudyRegister> studyRegisterList, List<Schedule> scheduleList, Member leader, String name, String content, Boolean secret, String password, Long fullCount, List<StudyCode> studyCodeList, IsUse isUse) {
        this.id = id;
        this.studyMemberList = studyMemberList;
        this.studyRegisterList = studyRegisterList;
        this.scheduleList = scheduleList;
        this.leader = leader;
        this.name = name;
        this.content = content;
        this.secret = secret;
        this.password = password;
        this.studyCodeList = studyCodeList;
        this.isUse = isUse;
        this.updateFullCount(fullCount);
    }

    public void changeToPrivate(String password) {
        if(this.secret) {
            throw new IllegalArgumentException("해당 스터디는 이미 비밀 스터디 입니다.");
        }
        if(StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("비밀 스터디에는 반드시 비밀번호가 포함되어야 합니다.");
        }
        this.secret = true;
        this.password = password;
    }

    public void changeToPublic(String password) {
        if(this.secret == null || !this.secret) {
            throw new IllegalArgumentException("해당 스터디는 비밀 스터디가 아닙니다.");
        }

        if(!this.password.equals(password)) {
            throw new IllegalArgumentException("스터디 전환 비밀번호가 틀렸습니다.");
        }
        this.secret = false;
        this.password = null;
    }

    public void addStudyMember(Member member) {
        this.studyMemberList.add(new StudyMember(member, this));
    }

    public Long getRemainCount() {
        if(this.studyMemberList == null) {
            return 0L;
        }
        return Long.valueOf(this.studyMemberList.size());
    }

    public Boolean isLeader(Member member) {
        return this.getLeader().getId().equals(member.getId());
    }

    public boolean isFull() {
        return this.getFullCount() - this.getRemainCount() <= 0;
    }

    public String getLeaderName() {
        return this.leader.getName();
    }

    public String getLeaderId() {
        return this.leader.getMemberId();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateFullCount(Long fullCount) {
        if(fullCount == null) {
            fullCount = 1L;
        }
        if(fullCount < 1 || fullCount > 100) {
            throw new IllegalArgumentException("인원 수는 반드시 1~100명 사이여야 합니다.");
        }
        if(this.getRemainCount() > fullCount) {
            throw new IllegalArgumentException("현재 인원보다 적은 수로 업데이트 할 수 없습니다.");
        }
        this.fullCount = fullCount;
    }
}
