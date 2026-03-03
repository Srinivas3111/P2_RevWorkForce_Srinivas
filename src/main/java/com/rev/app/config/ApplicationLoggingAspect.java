package com.rev.app.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationLoggingAspect
{
    private static final Logger LOGGER = LogManager.getLogger(ApplicationLoggingAspect.class);

    @Around("execution(* com.rev.app..*(..)) && !execution(* com.rev.app.config.ApplicationLoggingAspect.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        long startTime = System.currentTimeMillis();

        LOGGER.info("START {} args={}", method, joinPoint.getArgs().length);

        try {
            Object result = joinPoint.proceed();
            LOGGER.info("END {} durationMs={}", method, System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable ex) {
            LOGGER.error(
                    "ERROR {} durationMs={} message={}",
                    method,
                    System.currentTimeMillis() - startTime,
                    ex.getMessage(),
                    ex
            );
            throw ex;
        }
    }
}
