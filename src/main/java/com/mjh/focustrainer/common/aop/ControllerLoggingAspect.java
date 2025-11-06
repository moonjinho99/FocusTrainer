package com.mjh.focustrainer.common.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Pointcut("execution(public * com.mjh.focustrainer..controller..*(..))")
    public void allControllerMethods(){}

    @Before("allControllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Controller Start : {}.{}()",className, methodName);

        if(args.length > 0){
            for(Object arg : args) {
                log.info("Arg : {}",arg);
            }
        }

    }


}
