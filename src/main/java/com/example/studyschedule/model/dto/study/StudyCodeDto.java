package com.example.studyschedule.model.dto.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.StudyCode;
import com.example.studyschedule.utils.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class StudyCodeDto {

    private Long id;
    private String userId;
    private String inviteCode;
    private String createdAt;

    public static StudyCodeDto entityToDto(StudyCode studyCode) {
        Member useMember = studyCode.getUseMember();
        return StudyCodeDto.builder()
                .id(studyCode.getId())
                .userId(Objects.isNull(useMember) ? "" : useMember.getMemberId())
                .inviteCode(studyCode.getInviteCode())
                .createdAt(DateUtils.localDateTimeToString(studyCode.getCreatedAt()))
                .build();
    }
}
