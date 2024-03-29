package com.example.service.service.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class UpdateMemberProfile {

        private String name;
        private String password;
        private Integer age;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class CreateMember {

        private String memberId;
        private String password;
        private String name;
        private Integer age;
    }
}
