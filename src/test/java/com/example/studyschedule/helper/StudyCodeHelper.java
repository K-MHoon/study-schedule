package com.example.service.helper;

import com.example.service.entity.member.Member;
import com.example.service.entity.study.Study;
import com.example.service.entity.study.StudyCode;
import com.example.service.repository.study.StudyCodeRepository;
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
