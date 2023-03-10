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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Pagination> getPublicStudyList(@PageableDefault Pageable pageable) {
        log.info("[getPublicStudyList] call ");

        Pagination<List<StudyDto>> response = studyService.getPublicStudyList(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{study_id}")
    public ResponseEntity<StudyDto> getPublicStudyDetail(@PathVariable("study_id") Long studyId) {
        log.info("[getStudyDetail] call, studyId ={}", studyId);

        StudyDto response = studyService.getPublicStudyDetail(studyId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createPublicStudy(@RequestBody @Validated StudyControllerRequest.CreateStudyRequest request,
                                            Principal principal) {
        log.info("[createPublicStudyList] called by = {}", principal.getName());

        studyService.createPublicStudy(request);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{study_id}")
    public ResponseEntity deleteStudy(@PathVariable("study_id") Long studyId, Principal principal) {
        log.info("[deleteStudy] called by = {}", principal.getName());

        studyService.deleteStudy(studyId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudyMemberAll(@RequestBody @Validated StudyControllerRequest.DeleteStudyMemberAllRequest request, Principal principal) {
        log.info("[deleteStudyMemberAll] called by = {}, request IdList = {}", principal.getName(), request.getStudyList());

        studyService.deleteStudyMemberAll(request);
    }
}
