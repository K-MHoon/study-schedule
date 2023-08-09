package com.example.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.security.Principal;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.example.service.controller..*.*(..))")
    private void cut() {}

    @Before("cut()")
    public void LogMethodCall(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        StringBuilder sb = new StringBuilder();

        Method method = signature.getMethod();
        sb.append("[").append(method.getName()).append("] ");

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        sb.append("called by ").append(userId).append(", ");

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        sb.append("args = [");
        for (int i = 0; i < args.length; i++) {
            if(args[i] instanceof Principal) continue;
            sb.append(parameterNames[i]).append(" = ").append(args[i]);
            if(i != (args.length - 1)) {
                sb.append(",");
            }
        }
        sb.append("]");

        log.info("Request {}", sb);
    }
}
