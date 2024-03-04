package com.example.common.model.dto.security;


import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public final class JwtToken {

    private String token;
    private long expiredTime;

}
