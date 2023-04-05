package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ToString
public class StudyRegisterDto {

    private String goal;

    private String objective;

    private String comment;

    private MemberDto requestMember;

    public static StudyRegisterDto entityToDto(StudyRegister studyRegister) {
        return StudyRegisterDto.builder()
                .goal(studyRegister.getGoal())
                .objective(studyRegister.getObjective())
                .comment(studyRegister.getComment())
                .requestMember(MemberDto.entityToDtoSimple(studyRegister.getRequestMember()))
                .build();
    }
}
