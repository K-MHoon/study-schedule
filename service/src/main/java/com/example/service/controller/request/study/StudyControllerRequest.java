package com.example.service.controller.request.study;

import com.example.common.enums.IsUse;
import com.example.service.service.study.request.StudyServiceRequest;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "{study.name.not-blank}")
        private String studyName;

        @NotBlank(message = "{study.content.not-blank}")
        private String content;

        @NotNull(message = "{study.secret.not-null}")
        private Boolean secret;

        private String password;

        @NotNull(message = "{study.full-count.not-null}")
        private Long fullCount;

        @NotNull(message = "{study.is-use.not-null}")
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
    public static final class DeleteStudyMemberAll {

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
        private List<Long> studyList;

        public StudyServiceRequest.DeleteStudyMemberAll toServiceRequest() {
            return StudyServiceRequest.DeleteStudyMemberAll.builder()
                    .studyList(this.studyList)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeSecret {

        private Boolean secret;

        @NotBlank(message = "{study.password.not-blank}")
        private String password;

        public StudyServiceRequest.ChangeSecret toServiceRequest() {
            return StudyServiceRequest.ChangeSecret.builder()
                    .secret(this.secret)
                    .password(this.password)
                    .build();
        }
    }
}
