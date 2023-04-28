package com.example.studyschedule.model.dto.member;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.model.dto.schedule.TodoDto;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.dto.study.StudyMemberDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.studyschedule.utils.DateUtils.localDateTimeToString;

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
        return MemberDto.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .roles(member.getRoles())
                .createdAt(localDateTimeToString(member.getCreatedAt()))
                .updatedAt(localDateTimeToString(member.getUpdatedAt()))
                .build();
    }

    public static MemberDto entityToDtoSimple(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .build();
    }
}
