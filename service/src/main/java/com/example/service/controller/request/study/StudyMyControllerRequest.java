package com.example.service.controller.request.study;

import com.example.service.service.study.request.StudyMyServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyMyControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudy {

        @NotEmpty
        private String studyName;

        @NotEmpty
        private String content;

        @NotNull
        private Long fullCount;

        public StudyMyServiceRequest.UpdateStudy toServiceRequest() {
            return StudyMyServiceRequest.UpdateStudy.builder()
                    .studyName(this.studyName)
                    .content(this.content)
                    .fullCount(this.fullCount)
                    .build();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudyState {

        @NotEmpty
        private String state;

        public StudyMyServiceRequest.UpdateStudyState toServiceRequest() {
            return StudyMyServiceRequest.UpdateStudyState.builder()
                    .state(this.state)
                    .build();
        }
    }
}
