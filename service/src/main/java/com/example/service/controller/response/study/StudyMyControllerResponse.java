package com.example.service.controller.response.study;

import com.example.common.model.dto.study.StudyCodeDto;
import com.example.common.model.dto.study.StudyDto;
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
