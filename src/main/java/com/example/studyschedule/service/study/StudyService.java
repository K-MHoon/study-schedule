package com.example.studyschedule.service.study;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.entity.study.StudyMember;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    private final MemberCommonService memberCommonService;

    private final PasswordEncoder passwordEncoder;

    private final StudyMemberRepository studyMemberRepository;

    @Transactional(readOnly = true)
    public Pagination<List<StudyDto>> getPublicStudyList(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAllBySecretAndIsUse(false, IsUse.Y, pageable);
        List<StudyDto> data = studyPage.getContent().stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());
        return new Pagination<>(studyPage, data);
    }

    @Transactional
    public void createPublicStudy(StudyControllerRequest.CreateStudyRequest request) {
        Study newStudy = Study.ofPublic(memberCommonService.getLoggedInMember(),
                request.getStudyName(),
                request.getFullCount(),
                request.getIsUse());

        if(request.getSecret()) {
            newStudy.changeToPrivate(passwordEncoder.encode(request.getPassword()));
        }

        studyRepository.save(newStudy);
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Member member = memberCommonService.getLoggedInMember();
        Study study = studyRepository.findByIdAndLeaderAndIsUse(studyId, member, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("????????? ????????? ???????????? study??? ???????????? ????????????. id = " + studyId));
        studyRepository.delete(study);
    }

    @Transactional(readOnly = true)
    public StudyDto getPublicStudyDetail(Long studyId) {
        Study publicStudy = studyRepository.findByIdAndSecretAndIsUse(studyId, false, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ?????????????????? ????????? ????????? ?????????. id = " + studyId));

        return StudyDto.entityToDto(publicStudy);
    }

    @Transactional
    public void deleteStudyMemberAll(StudyControllerRequest.DeleteStudyMemberAllRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();

        List<StudyMember> studyMemberList = getRequestStudyMemberList(request, loggedInMember);
        if(studyMemberList.size() != request.getStudyList().size()) {
            throw new IllegalArgumentException("?????? ???????????? ????????? ??? ?????? ???????????? ???????????? ????????????. memberId = " + loggedInMember.getMemberId());
        }
        studyMemberRepository.deleteAllInBatch(studyMemberList);
    }

    private List<StudyMember> getRequestStudyMemberList(StudyControllerRequest.DeleteStudyMemberAllRequest request, Member loggedInMember) {
        return studyMemberRepository.findAllByStudy_IdInAndMember_Id(request.getStudyList(), loggedInMember.getId());
    }
}

