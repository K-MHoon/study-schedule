package com.example.service.controller.study;

import com.example.common.model.request.study.StudyControllerRequest;
import com.example.service.service.study.StudyRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/register")
public class StudyRegisterController {

    private final StudyRegisterService studyService;

    @PostMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public void createStudyRegister(
            @PathVariable("study_id") Long studyId,
            @RequestBody @Validated StudyControllerRequest.CreateStudyRegisterRequest request,
            Principal principal) {

        log.info("[createStudyRegister] called by {}, study Id = {}, request = {}", principal.getName(), studyId, request);

        studyService.createStudyRegister(studyId, request);
    }

    @PostMapping("/register/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelStudyRegisterAll(@RequestBody @Validated StudyControllerRequest.CancelStudyRegisterRequest request,
                                       Principal principal) {
        log.info("[cancelStudyRegisterAll] called by {}, studyRegister List = {}", principal.getName(), request.getStudyRegisterList());

        studyService.cancelStudyRegisterAll(request);
    }
}
