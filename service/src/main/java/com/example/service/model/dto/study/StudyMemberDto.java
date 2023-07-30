package com.example.service.model.dto.study;

import com.example.service.entity.study.Study;
import com.example.service.entity.study.StudyMember;
import com.example.service.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ToString
public class StudyMemberDto {

    private Long id;
    private Long studyId;
    private String studyName;
    private String studyCreatedBy;
    private String joinedBy;

    public static StudyMemberDto entityToDto(StudyMember studyMember) {
        Study study = studyMember.getStudy();

        return StudyMemberDto.builder()
                .id(studyMember.getId())
                .studyId(study.getId())
                .studyName(study.getName())
                .studyCreatedBy(DateUtils.localDateTimeToString(study.getCreatedAt()))
                .joinedBy(DateUtils.localDateTimeToString(studyMember.getCreatedAt()))
                .build();
    }
}
