package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.model.dto.member.MemberDto;
import com.example.studyschedule.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyDto {

    private Long id;
    private String leaderId;
    private String studyName;
    private String content;
    private Long remainCount;
    private Long fullCount;
    private IsUse isUse;
    private String createdAt;
    private Boolean isMine;
    private String joinedAt;
    private List<MemberDto> registeredMemberList;
    private List<StudyRegisterDto> registerRequestList;

    public static StudyDto entityToDto(Study study) {
        return StudyDto.builder()
                .id(study.getId())
                .leaderId(study.getLeaderId())
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
                .leaderId(study.getLeaderId())
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

    public static StudyDto entityToDtoDetail(StudyMember studyMember) {
        Study study = Objects.requireNonNull(studyMember.getStudy());
        Member member = Objects.requireNonNull(studyMember.getMember());
        boolean isMine = Objects.requireNonNull(study.getLeader()).equals(member);

        return StudyDto.builder()
                .id(study.getId())
                .leaderId(study.getLeaderId())
                .studyName(study.getName())
                .content(study.getContent())
                .remainCount(study.getRemainCount())
                .fullCount(study.getFullCount())
                .isUse(study.getIsUse())
                .createdAt(DateUtils.localDateTimeToString(study.getCreatedAt()))
                .isMine(isMine)
                .joinedAt(DateUtils.localDateTimeToString(studyMember.getCreatedAt()))
                .registeredMemberList(getRegisteredMemberDtoList(study))
                .registerRequestList(getStudyRegisterDtoList(study))
                .build();
    }

    private static List<StudyRegisterDto> getStudyRegisterDtoList(Study study) {
        return study.getStudyRegisterList().stream()
                .filter(studyRegister -> studyRegister.getState() == RegisterState.NO_READ || studyRegister.getState() == RegisterState.READ)
                .map(StudyRegisterDto::entityToDto)
                .collect(Collectors.toList());
    }

    private static List<MemberDto> getRegisteredMemberDtoList(Study study) {
        return study.getStudyMemberList().stream().map(StudyMember::getMember).map(MemberDto::entityToDtoSimple).collect(Collectors.toList());
    }
}
