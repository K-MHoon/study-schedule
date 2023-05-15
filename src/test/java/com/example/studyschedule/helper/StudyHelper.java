package com.example.studyschedule.helper;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.repository.study.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyHelper {

    @Autowired
    StudyRepository studyRepository;

    public Study createSimpleStudy(Member member) {
        return studyRepository.save(Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y));
    }
}
