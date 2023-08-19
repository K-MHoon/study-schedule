package com.example.common.entity.member;

import com.example.common.entity.common.BaseEntity;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.Todo;
import com.example.common.entity.study.StudyMember;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "study_member")
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    private String name;

    @Column(nullable = false)
    private Integer age;

    @Builder
    private Member(String memberId, String password, List<String> roles, String name, Integer age) {
        this.memberId = memberId;
        this.password = password;
        this.roles = roles;
        this.name = name;
        this.updateAge(age);
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Todo> todoList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<StudyMember> studyMemberList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) && Objects.equals(memberId, member.memberId) && Objects.equals(password, member.password);
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberId, password);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAge(Integer age) {
        if(age == null) {
            throw new IllegalArgumentException("나이는 null 값 일 수 없습니다.");
        }
        if(age < 1 || age > 100) {
            throw new IllegalArgumentException("나이는 1~100살 까지만 입력이 가능합니다.");
        }
        this.age = age;
    }
}
