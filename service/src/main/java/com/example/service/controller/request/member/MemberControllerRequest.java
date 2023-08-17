package com.example.service.controller.request.member;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberControllerRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateMemberRequest {

        @NotBlank(message = "회원 아이디는 빈 칸일 수 없습니다.")
        private String memberId;

        @NotBlank(message = "비밀번호는 빈 칸일 수 없습니다.")
        private String password;

        @NotBlank(message = "이름은 빈 칸일 수 없습니다.")
        private String name;

        @NotNull(message = "해당 값은 널(Null)일 수 없습니다.")
        private Integer age;
    }

    @Getter
    public static class LoginRequest {

        @NotBlank
        private String memberId;

        @NotBlank
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UpdateMemberProfileRequest {

        @NotBlank(message = "이름은 빈 칸일 수 없습니다.")
        private String name;

        private String password;

        @NotNull(message = "해당 값은 널(Null)일 수 없습니다.")
        private Integer age;
    }
}
