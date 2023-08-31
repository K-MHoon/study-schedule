package com.example.service.controller.request.study;

import com.example.service.service.study.request.StudyRegisterServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRegisterControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateStudyRegister {

        @NotEmpty
        private String goal;

        @NotEmpty
        private String objective;

        private String comment;

        public StudyRegisterServiceRequest.CreateStudyRegister toServiceRequest() {
            return StudyRegisterServiceRequest.CreateStudyRegister.builder()
                    .goal(this.goal)
                    .objective(this.objective)
                    .comment(this.comment)
                    .build();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelStudyRegister {

        @UniqueElements
        @NotNull
        private List<Long> studyRegisterList;

        public StudyRegisterServiceRequest.CancelStudyRegister toServiceRequest() {
            return StudyRegisterServiceRequest.CancelStudyRegister.builder()
                    .studyRegisterList(this.studyRegisterList)
                    .build();
        }
    }
}
