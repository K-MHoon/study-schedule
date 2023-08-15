package com.example.service.service.study;

import com.example.common.entity.member.Member;
import com.example.common.entity.study.Study;
import com.example.common.entity.study.StudyCode;
import com.example.common.entity.study.StudyMember;
import com.example.common.enums.IsUse;
import com.example.common.model.dto.Pagination;
import com.example.common.model.dto.study.StudyDto;
import com.example.service.controller.request.study.StudyControllerRequest;
import com.example.common.repository.study.StudyCodeRepository;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.common.repository.study.StudyRepository;
import com.example.service.service.member.MemberCommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    private final StudyCommonService studyCommonService;

    private final StudyCodeRepository studyCodeRepository;

    @Transactional(readOnly = true)
    public Pagination<List<StudyDto>> getPublicStudyList(StudyControllerRequest.GetPublicStudyListRequest request) {
        Page<Study> studyPage = studyRepository.findAllPublicStudyList(request);

        List<StudyDto> data = studyPage.getContent().stream()
                .map(StudyDto::entityToDto)
                .collect(Collectors.toList());

        return new Pagination<>(studyPage, data);
    }

    @Transactional
    public void createPublicStudy(StudyControllerRequest.CreateStudyRequest request) {
        Study newStudy = Study.ofPublic(memberCommonService.getLoggedInMember(),
                request.getStudyName(),
                request.getContent(),
                request.getFullCount(),
                request.getIsUse());

        if (request.getSecret()) {
            newStudy.changeToPrivate(passwordEncoder.encode(request.getPassword()));
        }

        Study savedStudy = studyRepository.save(newStudy);
        studyMemberRepository.save(new StudyMember(memberCommonService.getLoggedInMember(), savedStudy));
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Member member = memberCommonService.getLoggedInMember();

        Study study = studyRepository.findByIdAndLeaderAndIsUse(studyId, member, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("해당 Study는 삭제할 수 없습니다. id = " + studyId));

        studyRepository.delete(study);
    }

    @Transactional(readOnly = true)
    public StudyDto getPublicStudyDetail(Long studyId, String inviteCode) {
        boolean secret = StringUtils.hasText(inviteCode) ? true : false;
        if(secret) {
            Member loggedInMember = memberCommonService.getLoggedInMember();
            studyCodeRepository.findByInviteCodeAndUseMember_Id(inviteCode, loggedInMember.getId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 초대 코드 입니다."));
        }
        Study publicStudy = studyRepository.findByIdAndSecretAndIsUse(studyId, secret, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디 입니다."));

        return StudyDto.entityToDto(publicStudy);
    }

    @Transactional
    public void deleteStudyMemberAll(StudyControllerRequest.DeleteStudyMemberAllRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<StudyMember> studyMemberList = getRequestStudyMemberList(request, loggedInMember);

        if (sizeNotEqual(request, studyMemberList)) {
            throw new IllegalArgumentException("해당 사용자가 삭제할 수 없는 스터디를 포함하고 있습니다. memberId = " + loggedInMember.getMemberId());
        }

        studyMemberRepository.deleteAllInBatch(studyMemberList);
    }

    private boolean sizeNotEqual(StudyControllerRequest.DeleteStudyMemberAllRequest request, List<StudyMember> studyMemberList) {
        return studyMemberList.size() != request.getStudyList().size();
    }

    private List<StudyMember> getRequestStudyMemberList(StudyControllerRequest.DeleteStudyMemberAllRequest request, Member loggedInMember) {
        return studyMemberRepository.findAllByStudy_IdInAndMember_Id(request.getStudyList(), loggedInMember.getId());
    }

    @Transactional
    public Long findSecretStudy(String inviteCode) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        StudyCode studyCode = studyCodeRepository.findByInviteCodeAndUseMemberIsNull(inviteCode).orElseThrow(() -> new EntityNotFoundException("코드에 해당하는 스터디가 존재하지 않습니다."));
        studyCode.updateUseMember(loggedInMember);
        return studyCode.getStudy().getId();
    }

    @Transactional
    public void changeStudySecretOrPublic(Long studyId, StudyControllerRequest.ChangeSecretRequest request) {
        StudyMember studyMember = studyCommonService.getMyStudyMember(studyId);
        if(request.getSecret()) {
            studyMember.getStudy().changeToPrivate(request.getPassword());
        } else {
            studyMember.getStudy().changeToPublic(request.getPassword());
        }
    }


}

