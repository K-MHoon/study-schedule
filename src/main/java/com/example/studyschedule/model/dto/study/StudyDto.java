package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.utils.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
    private boolean isMine;

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
                .isMine(false)
                .build();
    }

    public void isMineTrue() {
        this.isMine = true;
    }
}
