package com.example.common.repository.study;

import com.example.common.entity.member.QMember;
import com.example.common.entity.study.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

import static com.example.common.entity.member.QMember.*;
import static com.example.common.entity.study.QStudy.*;
import static com.example.common.entity.study.QStudyMember.*;
import static com.example.common.entity.study.QStudyRegister.*;

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
