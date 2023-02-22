package com.example.studyschedule.model.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class TokenInfo {

    private String grantType;
    private String accessToken;
    private long accessExpiredTime;
    private String refreshToken;
    private long refreshExpiredTime;
}
