package com.example.common.entity.member;

import com.example.common.entity.common.BaseEntity;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.Todo;
import com.example.common.entity.study.StudyMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "study_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    private String name;

    private Integer age;

    public Member(String memberId, String password, List<String> roles, String name, Integer age) {
        this.memberId = memberId;
        this.password = password;
        this.roles = roles;
        this.name = name;
        this.age = age;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Todo> todoList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
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
        this.age = age;
    }
}
