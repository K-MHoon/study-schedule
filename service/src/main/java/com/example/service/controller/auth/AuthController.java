package com.example.service.controller.auth;


import com.example.common.model.dto.security.JwtToken;
import com.example.common.model.dto.security.TokenInfo;
import com.example.service.controller.response.auth.AuthControllerResponse;
import com.example.service.security.provider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/api/auth/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenInfo refresh(@RequestHeader("Authorization") String authHeader,
                             @RequestParam("refreshToken") String refreshToken) {

        if(!StringUtils.hasText(refreshToken)) {
            throw new IllegalArgumentException("갱신 토큰이 존재하지 않습니다.");
        }

        if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("헤더가 존재하지 않거나 잘못된 형식입니다.");
        }

        String accessToken = authHeader.substring(7);

        // 만료되지 않았으므로 토큰 유지
        if(jwtTokenProvider.validateToken(accessToken)) {
            return null;
        }

        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("잘못된 Refresh Token 입니다.");
        }

        Claims claims = jwtTokenProvider.parseClaims(refreshToken);

        JwtToken newAccessToken = jwtTokenProvider.generateAccessToken(refreshToken);
        JwtToken newRefreshToken = checkTime(claims.getExpiration().getTime()) ? jwtTokenProvider.generateRefreshToken(refreshToken) : new JwtToken(refreshToken, claims.getExpiration().getTime());

        return new TokenInfo(newAccessToken, newRefreshToken);
    }

    private boolean checkTime(long exp) {
        Date expDate = new Date(exp * (1000));

        long gap = expDate.getTime() - System.currentTimeMillis();

        long leftMin = gap / (1000 * 60);

        return leftMin < 60;
    }
}
