package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.Study;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ToString
public class StudyDto {

    private Long id;
    private String leaderName;
    private String studyName;
    private Long remainCount;
    private Long fullCount;
    private LocalDateTime createdBy;

    public static StudyDto entityToDto(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .leaderName(study.getLeader().getName())
                .studyName(study.getName())
                .remainCount(Long.valueOf(study.getStudyMemberList().stream().count()))
                .fullCount(study.getFullCount())
                .build();
    }
}
