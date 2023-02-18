package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}
