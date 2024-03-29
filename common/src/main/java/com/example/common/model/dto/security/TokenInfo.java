package com.example.common.model.dto.security;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public final class TokenInfo {

    private JwtToken accessToken;
    private JwtToken refreshToken;
}
