package com.example.service.controller.request.study;

import com.example.service.service.study.request.StudyMyServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyMyControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudy {

        @NotBlank(message = "{study.name.not-blank}")
        private String studyName;

        @NotBlank(message = "{study.content.not-blank}")
        private String content;

        @NotNull(message = "{study.full-count.not-null}")
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

        @NotBlank(message = "{study.state.not-blank}")
        private String state;

        public StudyMyServiceRequest.UpdateStudyState toServiceRequest() {
            return StudyMyServiceRequest.UpdateStudyState.builder()
                    .state(this.state)
                    .build();
        }
    }
}
