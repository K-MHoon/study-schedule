package com.example.service.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SecurityProperties {

    @Value("${security.login-processing-url}")
    private String loginProcessingUrl;

    @Value("${security.username-parameter}")
    private String usernameParameter;

    @Value("${security.password-parameter}")
    private String passwordParameter;

    public RequestMatcher getLoginProcessRequestMatcher() {
        return new AntPathRequestMatcher(this.loginProcessingUrl, "POST");
    }
}
