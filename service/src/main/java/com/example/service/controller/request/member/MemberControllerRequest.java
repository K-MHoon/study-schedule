package com.example.service.controller.request.member;

import com.example.service.service.request.MemberServiceRequest;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberControllerRequest {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateMember {

        @NotBlank(message = "회원 아이디는 빈 칸일 수 없습니다.")
        private String memberId;

        @NotBlank(message = "비밀번호는 빈 칸일 수 없습니다.")
        private String password;

        @NotBlank(message = "이름은 빈 칸일 수 없습니다.")
        private String name;

        @NotNull(message = "해당 값은 널(Null)일 수 없습니다.")
        private Integer age;

        public MemberServiceRequest.CreateMember toServiceRequest() {
            return MemberServiceRequest.CreateMember.builder()
                    .memberId(this.memberId)
                    .name(this.name)
                    .password(this.password)
                    .age(this.age)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UpdateMemberProfile {

        @NotBlank(message = "이름은 빈 칸일 수 없습니다.")
        private String name;

        private String password;

        @NotNull(message = "해당 값은 널(Null)일 수 없습니다.")
        private Integer age;

        public MemberServiceRequest.UpdateMemberProfile toServiceRequest() {
            return MemberServiceRequest.UpdateMemberProfile.builder()
                    .name(this.name)
                    .password(this.password)
                    .age(this.age)
                    .build();
        }
    }
}
