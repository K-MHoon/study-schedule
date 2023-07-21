package com.example.studyschedule.helper;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyCode;
import com.example.studyschedule.repository.study.StudyCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StudyCodeHelper {

    @Autowired
    StudyCodeRepository studyCodeRepository;

    public StudyCode createStudyCode(Member member, Study simpleStudy) {
        StudyCode studyCode = new StudyCode(simpleStudy);
        studyCode.updateUseMember(member);
        return studyCodeRepository.save(studyCode);
    }
}
