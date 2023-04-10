package com.example.studyschedule.model.request.study;

import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.enums.RegisterState;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyControllerRequest {

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
}
