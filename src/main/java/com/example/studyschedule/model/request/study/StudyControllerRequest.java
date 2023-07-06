package com.example.studyschedule.model.request.study;

import com.example.studyschedule.enums.IsUse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyControllerRequest {


    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    public static final class GetPublicStudyListRequest {

        private String name;
        private String leaderId;

        private Pageable pageable;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateStudyRequest {

        @NotEmpty
        private String studyName;

        @NotEmpty
        private String content;

        @NotNull
        private Boolean secret;

        private String password;

        @NotNull
        @Min(1)
        @Max(100)
        private Long fullCount;

        @NotNull
        private IsUse isUse;
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
