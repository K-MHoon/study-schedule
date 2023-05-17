package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.dto.study.StudyRegisterDto;
import com.example.studyschedule.model.request.study.StudyControllerRequest;
import com.example.studyschedule.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/study/my")
public class StudyMyController {

    private final StudyService studyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StudyDto> getMyStudy(Principal principal) {
        log.info("[getMyStudy] called by {}", principal.getName());

        return studyService.getMyStudy();
    }

    @GetMapping("/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDto getMyStudyDetail(@PathVariable Long studyId, Principal principal) {
        log.info("[getMyStudyDetail] called by {}", principal.getName());

        return studyService.getMyStudyDetail(studyId);
    }

    @PostMapping("/{studyId}/state/{registerId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStudyState(@PathVariable Long studyId,
                                 @PathVariable Long registerId,
                                 @RequestBody @Validated StudyControllerRequest.UpdateStudyStateRequest state,
                                 Principal principal) {
        log.info("[updateStudyReadState] called by {} state = {}", principal.getName(), state);

        studyService.updateStudyState(studyId, registerId, state);
    }

    @PostMapping("/{studyId}/member/{memberId}/out")
    @ResponseStatus(HttpStatus.OK)
    public void kickOutStudyMember(@PathVariable Long studyId, @PathVariable Long memberId,
                                   Principal principal) {
        log.info("[kickOutStudyMember] called by {}, studyId = {}, memberId = {}", principal.getName(), studyId, memberId);

        studyService.kickOutStudyMember(studyId, memberId);
    }

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList(Principal principal) {
        log.info("[getMyStudyRegisterRequestList] called by {}", principal.getName());

        return studyService.getMyStudyRegisterRequestList();
    }
}
