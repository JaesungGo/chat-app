package com.js.chat_app.service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("@annotation(performanceCheck)")
    public Object checkPerformance(ProceedingJoinPoint joinPoint, PerformanceCheck performanceCheck) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        long executionTime = (System.currentTimeMillis() - startTime);

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String annotationValue = performanceCheck.value();

        if (annotationValue.isEmpty()) {
            log.info("실행시간 측정 {}.{} => 실행시간: {}ms",
                    className, methodName, executionTime);
        } else {
            log.info("실행시간 측정 {}.{} ({}) => 실행시간: {}ms",
                    className, methodName, annotationValue, executionTime);
        }

        return result;
    }
}
