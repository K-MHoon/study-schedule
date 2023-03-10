package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
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
public class StudyMemberDto {

    private Long id;
    private Long studyId;
    private String studyName;
    private LocalDateTime studyCreatedBy;
    private LocalDateTime joinedBy;

    public static StudyMemberDto entityToDto(StudyMember studyMember) {
        Study study = studyMember.getStudy();

        return StudyMemberDto.builder()
                .id(studyMember.getId())
                .studyId(study.getId())
                .studyName(study.getName())
                .studyCreatedBy(study.getCreatedAt())
                .joinedBy(studyMember.getCreatedAt())
                .build();
    }
}
