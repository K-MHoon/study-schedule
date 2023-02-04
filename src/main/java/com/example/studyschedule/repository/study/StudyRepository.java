package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
