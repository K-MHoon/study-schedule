package com.example.service.security.provider;

import com.example.common.model.dto.security.JwtToken;
import com.example.common.model.dto.security.TokenInfo;
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

    public JwtToken generateAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpired);
    }

    public JwtToken generateAccessToken(String token) {
        Authentication authentication = getAuthentication(token);
        return createToken(authentication, accessTokenExpired);
    }

    public JwtToken generateRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpired);
    }

    public JwtToken generateRefreshToken(String token) {
        Authentication authentication = getAuthentication(token);
        return createToken(authentication, refreshTokenExpired);
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        if(claims.get("auth") == null) {
            throw new IllegalArgumentException("토큰에 권한 정보가 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().
                    parseClaimsJws(token)
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

    private JwtToken createToken(Authentication authentication, long expiredTime) {
        long currentTime = new Date().getTime();
        Date expiredDate = new Date(currentTime + expiredTime);

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", getAuthorities(authentication))
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new JwtToken(token, expiredDate.getTime());
    }
}
