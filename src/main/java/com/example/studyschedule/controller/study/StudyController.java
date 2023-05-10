package com.example.studyschedule.controller.study;

import com.example.studyschedule.model.dto.Pagination;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.model.dto.study.StudyRegisterDto;
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
    public Pagination<List<StudyDto>> getPublicStudyList(@PageableDefault Pageable pageable,
                                                         @RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "leader", required = false) String leader) {
        log.info("[getPublicStudyList] call ");

        StudyControllerRequest.GetPublicStudyListRequest request = StudyControllerRequest
                .GetPublicStudyListRequest.builder()
                .name(name)
                .leaderId(leader)
                .pageable(pageable)
                .build();

        return studyService.getPublicStudyList(request);
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

    @GetMapping("/my/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyDto getMyStudyDetail(@PathVariable Long studyId, Principal principal) {
        log.info("[getMyStudyDetail] called by {}", principal.getName());

        return studyService.getMyStudyDetail(studyId);
    }

    @PostMapping("/my/{studyId}/state/{registerId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStudyState(@PathVariable Long studyId,
                                 @PathVariable Long registerId,
                                 @RequestBody @Validated StudyControllerRequest.UpdateStudyStateRequest state,
                                 Principal principal) {
        log.info("[updateStudyReadState] called by {} state = {}", principal.getName(), state);

        studyService.updateStudyState(studyId, registerId, state);
    }

    @PostMapping("/my/{studyId}/member/{memberId}/out")
    @ResponseStatus(HttpStatus.OK)
    public void kickOutStudyMember(@PathVariable Long studyId, @PathVariable Long memberId,
                              Principal principal) {
        log.info("[kickOutStudyMember] called by {}, studyId = {}, memberId = {}", principal.getName(), studyId, memberId);

        studyService.kickOutStudyMember(studyId, memberId);
    }

    @GetMapping("/my/register")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyRegisterDto> getMyStudyRegisterRequestList(Principal principal) {
        log.info("[getMyStudyRegisterRequestList] called by {}", principal.getName());

        return studyService.getMyStudyRegisterRequestList();
    }
}
