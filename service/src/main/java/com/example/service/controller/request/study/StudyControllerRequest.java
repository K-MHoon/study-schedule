package com.example.service.controller.request.study;

import com.example.common.enums.IsUse;
import com.example.service.service.study.request.StudyServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateStudy {

        @NotEmpty
        private String studyName;

        @NotEmpty
        private String content;

        @NotNull
        private Boolean secret;

        private String password;

        @NotNull
        private Long fullCount;

        @NotNull
        private IsUse isUse;

        public StudyServiceRequest.CreateStudy toServiceRequest() {
            return StudyServiceRequest.CreateStudy.builder()
                    .studyName(this.studyName)
                    .content(this.content)
                    .secret(this.secret)
                    .password(this.password)
                    .fullCount(this.fullCount)
                    .isUse(this.isUse)
                    .build();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudyRequest {

        @NotEmpty
        private String studyName;

        @NotEmpty
        private String content;

        @NotNull
        private Long fullCount;

    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DeleteStudyMemberAllRequest {

        @NotNull
        @UniqueElements
        private List<Long> studyList;
    }
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateStudyRegisterRequest {

        @NotEmpty
        private String goal;

        @NotEmpty
        private String objective;

        private String comment;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudyStateRequest {

        @NotEmpty
        private String state;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelStudyRegisterRequest {

        @UniqueElements
        @NotNull
        private List<Long> studyRegisterList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeSecretRequest {

        private Boolean secret;

        @NotEmpty
        private String password;
    }
}
