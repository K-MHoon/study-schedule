package com.example.studyschedule.model.request.study;

import com.example.studyschedule.enums.IsUse;
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
}
