package com.example.service.controller.study;

import com.example.common.model.dto.study.StudyDto;
import com.example.common.model.dto.study.StudyRegisterDto;
import com.example.service.controller.request.study.StudyMyControllerRequest;
import com.example.service.controller.response.study.StudyMyControllerResponse;
import com.example.service.service.study.StudyMyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/my")
public class StudyMyController {

    private final StudyMyService studyMyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StudyDto> getMyStudy() {
        return studyMyService.getMyStudy();
    }

    @GetMapping("/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyMyControllerResponse.GetMyStudyDetailResponse getMyStudyDetail(@PathVariable Long studyId) {
        return studyMyService.getMyStudyDetail(studyId);
    }

    @PostMapping("/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMyStudy(@PathVariable("studyId") Long studyId,
                              @RequestBody @Validated StudyMyControllerRequest.UpdateStudy request) {
        studyMyService.updateMyStudy(studyId, request);
    }


    @PostMapping("/{studyId}/state/{registerId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStudyState(@PathVariable Long studyId,
                                 @PathVariable Long registerId,
                                 @RequestBody @Validated StudyMyControllerRequest.UpdateStudyState state) {
        studyMyService.updateStudyState(studyId, registerId, state);
    }

    @PostMapping("/{studyId}/member/{memberId}/out")
    @ResponseStatus(HttpStatus.OK)
    public void kickOutStudyMember(@PathVariable Long studyId, @PathVariable Long memberId) {
        studyMyService.kickOutStudyMember(studyId, memberId);
    }

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList() {
        return studyMyService.getMyStudyRegisterRequestList();
    }
}
