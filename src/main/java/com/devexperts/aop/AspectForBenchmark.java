package com.devexperts.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ifedorenkov
 */
@Aspect
public class AspectForBenchmark {

    @Around(value = "within(com.devexperts.service..*) && execution(* *(..)) && target(com.devexperts.service.PersonService)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

}
