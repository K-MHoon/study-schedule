package com.example.service.helper;

import com.example.common.entity.member.Member;
import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyMember;
import com.example.common.entity.study.StudyRegister;
import com.example.common.enums.IsUse;
import com.example.common.enums.RegisterState;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.common.repository.study.StudyRegisterRepository;
import com.example.common.repository.study.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class StudyHelper {

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyRegisterRepository studyRegisterRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    public Study createSimpleStudy(Member member) {
        return studyRepository.save(Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y));
    }

    public Study createStudyWithStudyMember(Member member) {
        Study study = Study.ofPublic(member, "스터디 테스트1", "스터디 설명", 10L, IsUse.Y);
        Study savedStudy = studyRepository.save(study);
        StudyMember studyMember = new StudyMember(member, savedStudy);
        studyMemberRepository.save(studyMember);
        return savedStudy;
    }

    public StudyMember joinStudy(Study study, Member member) {
        StudyMember studyMember = new StudyMember(member, study);
        return studyMemberRepository.save(studyMember);
    }

    public void createStudyMember(Study savedStudy, List<Member> memberList) {
        List<StudyMember> studyMemberList = memberList.stream()
                .map(m -> new StudyMember(m, savedStudy))
                .collect(Collectors.toList());
        studyMemberRepository.saveAll(studyMemberList);
    }

    public List<StudyRegister> createStudyRegister(Study savedStudy, List<Member> memberList) {
        return createStudyRegister(savedStudy, memberList, RegisterState.NO_READ);
    }

    public List<StudyRegister> createStudyRegister(Study savedStudy, List<Member> memberList, RegisterState registerState) {
        List<StudyRegister> studyRegisterList = memberList
                .stream()
                .map(m -> StudyRegister.builder()
                        .requestStudy(savedStudy)
                        .requestMember(m)
                        .goal("테스트")
                        .objective("테스트")
                        .state(registerState)
                        .comment("테스트").build())
                .collect(Collectors.toList());
        return studyRegisterRepository.saveAll(studyRegisterList);
    }

    public StudyRegister createStudyRegister(Study study, Member member, RegisterState registerState) {
        StudyRegister studyRegister = StudyRegister.builder()
                .requestStudy(study)
                .requestMember(member)
                .goal("테스트")
                .objective("테스트")
                .state(registerState)
                .comment("테스트").build();
        return studyRegisterRepository.save(studyRegister);
    }

}
