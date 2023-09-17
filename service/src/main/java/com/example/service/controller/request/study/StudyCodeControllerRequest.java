package com.example.service.controller.request.study;

import com.example.service.service.study.request.StudyCodeServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public final class StudyCodeControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DeleteInviteCodeAll {

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
        private List<Long> inviteCodeList;

        public StudyCodeServiceRequest.DeleteInviteCodeAll toServiceRequest() {
            return StudyCodeServiceRequest.DeleteInviteCodeAll.builder()
                    .inviteCodeList(this.inviteCodeList)
                    .build();
        }
    }

}
