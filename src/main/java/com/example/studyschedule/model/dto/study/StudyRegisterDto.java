package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ToString
public class StudyRegisterDto {

    private Long id;

    private String goal;

    private String objective;

    private String comment;

    private String createdAt;

    private String approvalAt;

    private String approvalBy;

    private String requestMemberId;

    public static StudyRegisterDto entityToDto(StudyRegister studyRegister) {
        Objects.requireNonNull(studyRegister);
        String approvalBy = studyRegister.getApprovalBy() == null ? null : studyRegister.getApprovalBy().getMemberId();
        String approvalAt = studyRegister.getApprovalAt() == null ? null : DateUtils.localDateTimeToString(studyRegister.getApprovalAt());

        return StudyRegisterDto.builder()
                .id(studyRegister.getId())
                .goal(studyRegister.getGoal())
                .objective(studyRegister.getObjective())
                .comment(studyRegister.getComment())
                .requestMemberId(studyRegister.getRequestMember().getMemberId())
                .createdAt(DateUtils.localDateTimeToString(studyRegister.getCreatedAt()))
                .approvalAt(approvalAt)
                .approvalBy(approvalBy)
                .build();
    }
}
