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
        log.debug("[getPublicStudyList] call ");

        Pagination<List<StudyDto>> response = studyService.getPublicStudyList(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createPublicStudy(@RequestBody @Validated StudyControllerRequest.CreateStudyRequest request,
                                            Principal principal) {
        log.debug("[createPublicStudyList] called by = {}", principal.getName());

        studyService.createPublicStudy(request);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{study_id}")
    public ResponseEntity deleteStudy(@PathVariable("study_id") Long studyId, Principal principal) {
        log.debug("[deleteStudy] called by = {}", principal.getName());

        studyService.deleteStudy(studyId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
