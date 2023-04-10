package com.example.studyschedule.repository.study;

import com.example.studyschedule.entity.study.StudyMember;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

import static com.example.studyschedule.entity.member.QMember.member;
import static com.example.studyschedule.entity.study.QStudy.study;
import static com.example.studyschedule.entity.study.QStudyMember.studyMember;
import static com.example.studyschedule.entity.study.QStudyRegister.studyRegister;


public class StudyMemberRepositorySupportImpl extends QuerydslRepositorySupport implements StudyMemberRepositorySupport {

    public StudyMemberRepositorySupportImpl() {
        super(StudyMember.class);
    }

    @Override
    public Optional<StudyMember> findMyStudyMember(Long studyId, Long memberId) {
        StudyMember sm = from(studyMember)
                .leftJoin(studyMember.member, member)
                .fetchJoin()
                .leftJoin(studyMember.study, study)
                .fetchJoin()
                .leftJoin(studyMember.study.studyRegisterList, studyRegister)
                .fetchJoin()
                .where(studyMember.study.id.eq(studyId), studyMember.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(sm);
    }
}
