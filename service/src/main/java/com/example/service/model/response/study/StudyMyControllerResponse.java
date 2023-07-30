package com.example.service.model.response.study;

import com.example.service.model.dto.study.StudyCodeDto;
import com.example.service.model.dto.study.StudyDto;
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
