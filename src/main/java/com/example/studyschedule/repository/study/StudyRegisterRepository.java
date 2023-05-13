package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.RegisterState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRegisterRepository extends JpaRepository<StudyRegister, Long> {

    Optional<StudyRegister> findByIdAndRequestStudy_Id(Long id, Long studyId);

    @EntityGraph(attributePaths = {"requestStudy"})
    List<StudyRegister> findAllByRequestMember_Id(Long id);

    int countAllByIdInAndRequestMember_Id(List<Long> studyRegisterIdList, Long memberId);

    @Modifying
    @Query("update StudyRegister sr " +
            "set sr.state = :state " +
            "where sr.id in (:studyRegisterIdList) " +
            "and sr.requestMember.id = :memberId")
    int updateAllCancelStudyRegister(@Param("state") RegisterState state,
                                     @Param("studyRegisterIdList") List<Long> studyRegisterIdList,
                                     @Param("memberId") Long memberId);

}
