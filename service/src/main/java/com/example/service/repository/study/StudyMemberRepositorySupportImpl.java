package com.example.service.repository.study;

import com.example.service.entity.study.Study;
import com.example.service.entity.study.StudyMember;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

import static com.example.service.entity.member.QMember.member;
import static com.example.service.entity.study.QStudy.study;
import static com.example.service.entity.study.QStudyMember.studyMember;
import static com.example.service.entity.study.QStudyRegister.studyRegister;


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

    @Override
    public List<Study> findAllMyStudy(Long memberId) {
        return from(studyMember)
                .select(studyMember.study)
                .leftJoin(studyMember.member, member)
                .leftJoin(studyMember.study, study)
                .where(studyMember.member.id.eq(memberId))
                .fetch();
    }
}
