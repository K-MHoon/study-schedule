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

        @NotBlank(message = "{member.id.not-blank}")
        private String memberId;

        @NotBlank(message = "{member.password.not-blank}")
        private String password;

        @NotBlank(message = "{member.name.not-blank}")
        private String name;

        @NotNull(message = "{member.age.not-null}")
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

        @NotBlank(message = "{member.name.not-blank}")
        private String name;

        private String password;

        @NotNull(message = "{member.age.not-null}")
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
