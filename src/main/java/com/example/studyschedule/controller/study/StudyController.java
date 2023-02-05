package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    public ResponseEntity<List<StudyDto>> getPublicStudyList() {
        log.debug("[getPublicStudyList] call ");

        List<StudyDto> response = studyService.getPublicStudyList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
