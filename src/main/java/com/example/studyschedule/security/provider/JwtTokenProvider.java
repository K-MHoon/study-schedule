package com.example.studyschedule.security.provider;

import com.example.studyschedule.model.dto.security.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.access_token.expired}")
    private long accessTokenExpired;

    @Value("${jwt.refresh_token.expired}")
    private long refreshTokenExpired;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateToken(Authentication authentication) {
        long currentTime = new Date().getTime();

        String accessToken = createAccessToken(authentication, currentTime);
        Token refreshToken = createRefreshToken(currentTime);

        /**
         * access_token과 refresh_token의 실제 유효기간은 분리 되어 있지만,
         * 클라이언트 쿠키는 refresh_token의 만료 시간으로 공통되게 생성할 수 있도록 한다.
         */
        return new TokenInfo("Bearer", accessToken, refreshToken.getToken(), refreshToken.getExpiredTime());
    }

    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if(claims.get("auth") == null) {
            throw new IllegalArgumentException("토큰에 권한 정보가 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().
                    parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Token createRefreshToken(long currentTime) {
        Date expiredDate = new Date(currentTime + refreshTokenExpired);

        String token = Jwts.builder()
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new Token(token, expiredDate.getTime());
    }

    private String createAccessToken(Authentication authentication, long currentTime) {
        Date expiredDate = new Date(currentTime + accessTokenExpired);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", getAuthorities(authentication))
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Getter
    private static class Token {

        private String token;
        private long expiredTime;

        public Token(String token, long expiredTime) {
            this.token = token;
            this.expiredTime = expiredTime;
        }
    }
}
