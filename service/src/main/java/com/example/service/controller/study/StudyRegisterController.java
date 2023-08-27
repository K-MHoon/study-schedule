package com.example.service.controller.study;

import com.example.service.controller.request.study.StudyRegisterControllerRequest;
import com.example.service.service.study.StudyRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/register")
public class StudyRegisterController {

    private final StudyRegisterService studyService;

    @PostMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public void createStudyRegister(@PathVariable("study_id") Long studyId, @RequestBody @Validated StudyRegisterControllerRequest.CreateStudyRegister request){
        studyService.createStudyRegister(studyId, request);
    }

    @PostMapping("/register/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelStudyRegisterAll(@RequestBody @Validated StudyRegisterControllerRequest.CancelStudyRegister request) {
        studyService.cancelStudyRegisterAll(request);
    }
}
