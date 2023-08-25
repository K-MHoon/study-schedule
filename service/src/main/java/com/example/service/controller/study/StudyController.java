package com.example.service.controller.study;

import com.example.common.model.dto.Pagination;
import com.example.common.model.dto.study.StudyDto;
import com.example.service.controller.request.study.StudyControllerRequest;
import com.example.service.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return studyService.getPublicStudyList(name, leader, pageable);
    }

    @GetMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDto getPublicStudyDetail(@PathVariable("study_id") Long studyId, @RequestParam(value = "invite-code", required = false) String inviteCode) {
        return studyService.getPublicStudyDetail(studyId, inviteCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPublicStudy(@RequestBody @Validated StudyControllerRequest.CreateStudy request) {
        studyService.createPublicStudy(request.toServiceRequest());
    }

    @PostMapping("/{study_id}/secret")
    @ResponseStatus(HttpStatus.OK)
    public void changeSecret(@PathVariable("study_id") Long studyId,
                             @RequestBody @Validated StudyControllerRequest.ChangeSecret request) {
        studyService.changeStudySecretOrPublic(studyId, request.toServiceRequest());
    }

    @DeleteMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudy(@PathVariable("study_id") Long studyId) {
        studyService.deleteStudy(studyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudyMemberAll(@RequestBody @Validated StudyControllerRequest.DeleteStudyMemberAll request) {
        studyService.deleteStudyMemberAll(request.toServiceRequest());
    }

    @GetMapping("/secret")
    @ResponseStatus(HttpStatus.OK)
    public Long findSecretStudy(@RequestParam("invite-code") String inviteCode) {
        return studyService.findSecretStudy(inviteCode);
    }
}
