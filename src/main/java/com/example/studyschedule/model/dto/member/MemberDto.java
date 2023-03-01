package com.example.studyschedule.model.dto.member;

import com.example.studyschedule.entity.member.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberDto entityToDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .roles(member.getRoles())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
