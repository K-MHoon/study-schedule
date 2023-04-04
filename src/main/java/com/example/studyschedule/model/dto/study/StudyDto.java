package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.utils.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class StudyDto {

    private Long id;
    private String leaderName;
    private String studyName;
    private String content;
    private Long remainCount;
    private Long fullCount;
    private IsUse isUse;
    private String createdAt;
    private boolean isMine;
    private String joinedAt;

    public static StudyDto entityToDto(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .leaderName(study.getLeaderName())
                .studyName(study.getName())
                .content(study.getContent())
                .remainCount(study.getRemainCount())
                .fullCount(study.getFullCount())
                .isUse(study.getIsUse())
                .createdAt(DateUtils.localDateTimeToString(study.getCreatedAt()))
                .build();
    }

    public static StudyDto entityToDto(StudyMember studyMember) {
        Study study = Objects.requireNonNull(studyMember.getStudy());
        Member member = Objects.requireNonNull(studyMember.getMember());
        boolean isMine = Objects.requireNonNull(study.getLeader()).equals(member);

        return StudyDto.builder()
                .id(study.getId())
                .leaderName(study.getLeaderName())
                .studyName(study.getName())
                .content(study.getContent())
                .remainCount(study.getRemainCount())
                .fullCount(study.getFullCount())
                .isUse(study.getIsUse())
                .createdAt(DateUtils.localDateTimeToString(study.getCreatedAt()))
                .isMine(isMine)
                .joinedAt(DateUtils.localDateTimeToString(studyMember.getCreatedAt()))
                .build();
    }
}
