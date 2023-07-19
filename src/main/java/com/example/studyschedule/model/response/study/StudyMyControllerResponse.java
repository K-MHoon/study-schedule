package com.example.studyschedule.model.response.study;

import com.example.studyschedule.model.dto.study.StudyCodeDto;
import com.example.studyschedule.model.dto.study.StudyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public final class StudyMyControllerResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyStudyDetailResponse {

        private StudyDto study;
        private List<StudyCodeDto> studyCodeList;
    }
}
