package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.request.study.StudyControllerRequest;
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

    @PostMapping("/{study_id}/code")
    @ResponseStatus(HttpStatus.OK)
    public void createInviteCode(@PathVariable(name = "study_id") Long studyId, Principal principal) {
        log.info("[createStudyInviteCode] called by {}, study Id = {}", principal.getName(), studyId);

        service.createInviteCode(studyId);
    }

    @DeleteMapping("/{study_id}/code")
    @ResponseStatus(HttpStatus.OK)
    public void deleteInviteCodeAll(@PathVariable(name = "study_id") Long studyId, @RequestBody @Validated StudyControllerRequest.DeleteInviteCodeAllRequest request, Principal principal) {
        log.info("[deleteInviteCodeAll] called by {}, study Id = {}", principal.getName(), studyId);

        service.deleteInviteCodeAll(studyId, request);
    }
}
