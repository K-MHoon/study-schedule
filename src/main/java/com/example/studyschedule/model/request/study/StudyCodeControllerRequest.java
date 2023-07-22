package com.example.studyschedule.model.request.study;

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
    public static final class DeleteInviteCodeAllRequest {

        @NotNull
        @UniqueElements
        private List<Long> inviteCodeList;
    }

}
