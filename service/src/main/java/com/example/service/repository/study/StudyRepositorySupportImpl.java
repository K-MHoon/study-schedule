package com.example.service.repository.study;

import com.example.service.entity.study.Study;
import com.example.service.enums.IsUse;
import com.example.service.model.request.study.StudyControllerRequest;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import static com.example.service.entity.member.QMember.member;
import static com.example.service.entity.study.QStudy.study;

public class StudyRepositorySupportImpl extends QuerydslRepositorySupport implements StudyRepositorySupport {
    public StudyRepositorySupportImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findAllPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request) {
        QueryResults<Study> result = from(study)
                .select(study)
                .leftJoin(study.leader, member)
                .where(studyNameContains(request.getName())
                        , studyLeaderIdContains(request.getLeaderId())
                        , study.isUse.eq(IsUse.Y)
                        , study.secret.isFalse())
                .offset(request.getPageable().getOffset())
                .limit(request.getPageable().getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), request.getPageable(), result.getTotal());
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
