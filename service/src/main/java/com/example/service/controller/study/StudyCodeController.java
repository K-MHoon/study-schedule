package com.example.service.controller.study;

import com.example.common.model.request.study.StudyCodeControllerRequest;
import com.example.common.model.response.study.StudyCodeControllerResponse;
import com.example.service.service.study.StudyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/{study_id}/code")
public class StudyCodeController {

    private final StudyCodeService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public StudyCodeControllerResponse.GetStudyCodeListResponse getInviteCodeList(@PathVariable(name = "study_id") Long studyId) {
        return service.getInviteCodeList(studyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createInviteCode(@PathVariable(name = "study_id") Long studyId) {
        service.createInviteCode(studyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteInviteCodeAll(@PathVariable(name = "study_id") Long studyId, @RequestBody @Validated StudyCodeControllerRequest.DeleteInviteCodeAllRequest request) {
        service.deleteInviteCodeAll(studyId, request);
    }
}
