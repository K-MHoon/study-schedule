package com.example.studyschedule.helper;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.entity.study.StudyRegister;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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
        StudyMember studyMember = new StudyMember(member, study);
        studyMemberRepository.save(studyMember);
        Study savedStudy = studyRepository.save(study);
        return savedStudy;
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
