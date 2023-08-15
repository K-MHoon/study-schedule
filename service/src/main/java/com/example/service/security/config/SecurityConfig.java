package com.example.service.security.config;

import com.example.service.security.SecurityProperties;
import com.example.service.security.filter.JwtAuthenticationFilter;
import com.example.service.security.filter.LoginFilter;
import com.example.service.security.handler.LoginFailureHandler;
import com.example.service.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/member/**", "/api/members", "/api/study/my", "api/study/register/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/study").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/study").hasRole("USER")
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private LoginFilter loginFilter() {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setRequiresAuthenticationRequestMatcher(securityProperties.getLoginProcessRequestMatcher());
        loginFilter.setAuthenticationManager(authenticationManagerBuilder.getObject());
        loginFilter.setUsernameParameter(securityProperties.getUsernameParameter());
        loginFilter.setPasswordParameter(securityProperties.getPasswordParameter());
        loginFilter.setAllowSessionCreation(false);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return loginFilter;
    }
}
