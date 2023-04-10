package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.StudyRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRegisterRepository extends JpaRepository<StudyRegister, Long> {

    Optional<StudyRegister> findByIdAndRequestStudy_Id(Long id, Long studyId);
}
