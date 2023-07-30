package com.example.service.model.dto.security;

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
    private String refreshToken;
    private long expiredTime;
}
