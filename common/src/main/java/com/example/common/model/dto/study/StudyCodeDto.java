package com.example.common.model.dto.study;

import com.example.common.utils.DateUtils;
import com.example.common.entity.member.Member;
import com.example.common.entity.study.StudyCode;
import com.example.common.utils.DateUtils;
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
        return builder()
                .id(studyCode.getId())
                .userId(Objects.isNull(useMember) ? "" : useMember.getMemberId())
                .inviteCode(studyCode.getInviteCode())
                .createdAt(DateUtils.localDateTimeToString(studyCode.getCreatedAt()))
                .build();
    }
}
