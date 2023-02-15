package com.example.studyschedule.model.request.member;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberControllerRequest {
    @Getter
    @AllArgsConstructor
    public static class CreateMemberRequest {

        @NotBlank
        private String memberId;

        @NotBlank
        private String password;

        @NotBlank
        private String name;

        @Positive
        @NotNull
        private Integer age;
    }

    @Getter
    public static class LoginRequest {

        @NotBlank
        private String memberId;

        @NotBlank
        private String password;
    }
}
