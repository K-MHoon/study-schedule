package com.example.studyschedule.model.dto.security;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public final class TokenInfo {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
