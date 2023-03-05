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
    private List<StudyMemberDto> joinedStudyList;
    private List<ScheduleDto> scheduleList;
    private List<TodoDto> todoList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // TODO N+1 문제 발생, QueryDsl로 개선 예정
    public static MemberDto entityToDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .age(member.getAge())
                .roles(member.getRoles())
                .joinedStudyList(toStudyMemberDtoList(member.getStudyMemberList()))
                .scheduleList(toScheduleDtoList(member.getScheduleList()))
                .todoList(toTodoDtoList(member.getTodoList()))
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    private static List<StudyMemberDto> toStudyMemberDtoList(List<StudyMember> studyMemberList) {
        return studyMemberList.stream()
                .map(StudyMemberDto::entityToDto)
                .collect(Collectors.toList());
    }

    private static List<ScheduleDto> toScheduleDtoList(List<Schedule> scheduleList) {
        return scheduleList.stream()
                .map(ScheduleDto::entityToDto)
                .collect(Collectors.toList());
    }

    private static List<TodoDto> toTodoDtoList(List<Todo> todoList) {
        return todoList.stream()
                .map(TodoDto::entityToDto)
                .collect(Collectors.toList());
    }
}
