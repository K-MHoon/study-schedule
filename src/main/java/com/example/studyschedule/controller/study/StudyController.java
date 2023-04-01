package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.service.study.StudyService;
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
    public Pagination<List<StudyDto>> getPublicStudyList(@PageableDefault Pageable pageable) {
        log.info("[getPublicStudyList] call ");

        return studyService.getPublicStudyList(pageable);
    }

    @GetMapping("/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDto getPublicStudyDetail(@PathVariable("study_id") Long studyId) {
        log.info("[getStudyDetail] call, studyId = {}", studyId);

        return studyService.getPublicStudyDetail(studyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPublicStudy(@RequestBody @Validated StudyControllerRequest.CreateStudyRequest request,
                                            Principal principal) {
        log.info("[createPublicStudyList] called by {}", principal.getName());

        studyService.createPublicStudy(request);
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

    @PostMapping("/register/{study_id}")
    @ResponseStatus(HttpStatus.OK)
    public void createStudyRegister(
            @PathVariable("study_id") Long studyId,
            @RequestBody @Validated StudyControllerRequest.CreateStudyRegisterRequest request,
            Principal principal) {

        log.info("[createStudyRegister] called by {}, study Id = {}, request = {}", principal.getName(), studyId, request);

        studyService.createStudyRegister(studyId, request);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyDto> getMyStudy(Principal principal) {
        log.info("[getMyStudy] called by {}", principal.getName());

        return studyService.getMyStudy();
    }
}
