package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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
    private String createdBy;

    public static StudyDto entityToDto(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .leaderName(study.getLeaderName())
                .studyName(study.getName())
                .content(study.getContent())
                .remainCount(study.getRemainCount())
                .fullCount(study.getFullCount())
                .isUse(study.getIsUse())
                .createdBy(DateUtils.localDateTimeToString(study.getCreatedAt()))
                .build();
    }
}
