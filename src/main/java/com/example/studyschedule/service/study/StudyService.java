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
    public StudyDto getPublicStudyDetail(Long studyId) {
        Study publicStudy = studyRepository.findByIdAndSecretAndIsUse(studyId, false, IsUse.Y)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스터디이거나 비공개 스터디 입니다. id = " + studyId));

        return StudyDto.entityToDto(publicStudy);
    }

    @Transactional
    public void deleteStudyMemberAll(StudyControllerRequest.DeleteStudyMemberAllRequest request) {
        Member loggedInMember = memberCommonService.getLoggedInMember();
        List<StudyMember> studyMemberList = getRequestStudyMemberList(request, loggedInMember);

        if(sizeNotEqual(request, studyMemberList)) {
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
}

