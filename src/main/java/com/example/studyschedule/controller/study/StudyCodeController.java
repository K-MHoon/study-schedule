package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.request.study.StudyCodeControllerRequest;
import com.example.studyschedule.model.response.study.StudyCodeControllerResponse;
import com.example.studyschedule.service.study.StudyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/{study_id}/code")
public class StudyCodeController {

    private final StudyCodeService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public StudyCodeControllerResponse.GetStudyCodeListResponse getInviteCodeList(@PathVariable(name = "study_id") Long studyId, Principal principal) {
        log.info("[getInviteCodeList] called by {}, study Id = {}", principal.getName(), studyId);

        return service.getInviteCodeList(studyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createInviteCode(@PathVariable(name = "study_id") Long studyId, Principal principal) {
        log.info("[createStudyInviteCode] called by {}, study Id = {}", principal.getName(), studyId);

        service.createInviteCode(studyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteInviteCodeAll(@PathVariable(name = "study_id") Long studyId, @RequestBody @Validated StudyCodeControllerRequest.DeleteInviteCodeAllRequest request, Principal principal) {
        log.info("[deleteInviteCodeAll] called by {}, study Id = {}", principal.getName(), studyId);

        service.deleteInviteCodeAll(studyId, request);
    }
}
