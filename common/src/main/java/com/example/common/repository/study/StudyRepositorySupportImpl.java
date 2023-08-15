package com.example.common.repository.study;

import com.example.common.entity.member.QMember;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsUse;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import static com.example.common.entity.study.QStudy.study;

public class StudyRepositorySupportImpl extends QuerydslRepositorySupport implements StudyRepositorySupport {
    public StudyRepositorySupportImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findAllPublicStudyList(String name, String leaderId, Pageable pageable) {
        QueryResults<Study> result = from(study)
                .select(study)
                .leftJoin(study.leader, QMember.member)
                .where(studyNameContains(name)
                        , studyLeaderIdContains(leaderId)
                        , study.isUse.eq(IsUse.Y)
                        , study.secret.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private BooleanExpression studyLeaderIdContains(String leaderId) {
        if(!StringUtils.hasText(leaderId)) return null;
        return study.leader.memberId.contains(leaderId);
    }

    private BooleanExpression studyNameContains(String name) {
        if(!StringUtils.hasText(name)) return null;
        return study.name.contains(name);
    }
}
