package com.example.common.model.dto.member;

import com.example.common.utils.DateUtils;
import com.example.common.entity.member.Member;
import com.example.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@ToString
public class MemberDto {

    private Long id;
    private String memberId;
    private String name;
    private Integer age;
    private List<String> roles;
    private String createdAt;
    private String updatedAt;

    public static MemberDto entityToDto(Member member) {
        return builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .roles(member.getRoles())
                .createdAt(DateUtils.localDateTimeToString(member.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(member.getUpdatedAt()))
                .build();
    }

    public static MemberDto entityToDtoSimple(Member member) {
        return builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .build();
    }
}
