package com.example.studyschedule.model.request.study;

import com.example.studyschedule.enums.IsUse;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyControllerRequest {

    @Getter
    @ToString
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
}