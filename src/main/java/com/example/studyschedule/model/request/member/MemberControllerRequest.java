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

        @NotBlank(message = "회원 아이디는 빈 칸일 수 없습니다.")
        private String memberId;

        @NotBlank(message = "비밀번호는 빈 칸일 수 없습니다.")
        private String password;

        @NotBlank(message = "이름은 빈 칸일 수 없습니다.")
        private String name;

        @Positive(message = "양수 입력만 가능합니다.")
        @NotNull(message = "해당 값은 널(Null)일 수 없습니다.")
        @Min(value = 1, message = "최소 1살 이상이어야 합니다.")
        @Max(value = 100, message = "최소 100살 미만이어야 합니다.")
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
