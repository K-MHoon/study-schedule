package com.example.service.controller.response.auth;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthControllerResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    @ToString
    @Builder
    public static class Refresh {
        private String accessToken;
        private String refreshToken;
    }
}
