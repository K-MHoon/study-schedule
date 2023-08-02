package com.example.service.helper;

import com.example.common.entity.member.Member;
import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyCode;
import com.example.common.repository.study.StudyCodeRepository;
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
