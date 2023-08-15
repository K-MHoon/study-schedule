package com.example.service.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String memberId = request.getParameter(super.getUsernameParameter());
        String password = request.getParameter(super.getPasswordParameter());

        return super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(memberId, password));
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response)
                && StringUtils.hasText(request.getParameter(super.getUsernameParameter()))
                && StringUtils.hasText(request.getParameter(super.getPasswordParameter()));
    }
}
