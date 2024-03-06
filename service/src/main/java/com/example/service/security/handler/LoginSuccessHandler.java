package com.example.service.security.handler;

import com.example.common.model.dto.security.JwtToken;
import com.example.common.model.dto.security.TokenInfo;
import com.example.service.security.provider.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JwtToken accessToken = jwtTokenProvider.generateAccessToken(authentication);
        JwtToken refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);

        String tokenString = objectMapper.writeValueAsString(tokenInfo);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(tokenString);
    }
}
