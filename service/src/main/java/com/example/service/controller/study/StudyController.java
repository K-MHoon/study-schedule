package com.example.service.controller.study;

import com.example.common.model.dto.Pagination;
import com.example.common.model.dto.study.StudyDto;
import com.example.common.model.request.study.StudyControllerRequest;
import com.example.service.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Pagination<List<StudyDto>> getPublicStudyList(@PageableDefault Pageable pageable,
                                                         @RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "leader", required = false) String leader) {
        log.info("[getPublicStudyList] call ");

        StudyControllerRequest.GetPublicStudyListRequest request = StudyControllerRequest
                .GetPublicStudyListRequest.builder()
                .name(name)
                .leaderId(leader)
                .pageable(pageable)
                .build();

        return studyService.getPublicStudyList(request);
    }

    @GetMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDto getPublicStudyDetail(@PathVariable("study_id") Long studyId, @RequestParam(value = "invite-code", required = false) String inviteCode) {
        log.info("[getStudyDetail] call, studyId = {}, inviteCode", studyId, inviteCode);

        return studyService.getPublicStudyDetail(studyId, inviteCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPublicStudy(@RequestBody @Validated StudyControllerRequest.CreateStudyRequest request,
                                            Principal principal) {
        log.info("[createPublicStudyList] called by {}", principal.getName());

        studyService.createPublicStudy(request);
    }

    @PostMapping("/{study_id}/secret")
    @ResponseStatus(HttpStatus.OK)
    public void changeSecret(Principal principal,
                             @PathVariable("study_id") Long studyId,
                             @RequestBody @Validated StudyControllerRequest.ChangeSecretRequest request) {
        log.info("[changeSecret] called by {}, studyId = {}", principal.getName(), studyId);

        studyService.changeStudySecretOrPublic(studyId, request);
    }

    @DeleteMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudy(@PathVariable("study_id") Long studyId, Principal principal) {
        log.info("[deleteStudy] called by {}", principal.getName());

        studyService.deleteStudy(studyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudyMemberAll(@RequestBody @Validated StudyControllerRequest.DeleteStudyMemberAllRequest request, Principal principal) {
        log.info("[deleteStudyMemberAll] called by {}, request IdList = {}", principal.getName(), request.getStudyList());

        studyService.deleteStudyMemberAll(request);
    }

    @GetMapping("/secret")
    @ResponseStatus(HttpStatus.OK)
    public Long findSecretStudy(Principal principal, @RequestParam("invite-code") String inviteCode) {
        log.info("[findSecretStudy] called by {} ", principal.getName());

        return studyService.findSecretStudy(inviteCode);
    }
}