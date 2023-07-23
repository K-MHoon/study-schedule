package com.example.studyschedule.model.response.study;

import com.example.studyschedule.model.dto.study.StudyCodeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyCodeControllerResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class GetStudyCodeListResponse {
        private List<StudyCodeDto> studyCodeList = new ArrayList<>();
    }
}
