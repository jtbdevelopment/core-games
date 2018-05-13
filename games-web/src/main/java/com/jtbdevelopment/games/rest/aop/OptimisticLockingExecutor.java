package com.jtbdevelopment.games.rest.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

/**
 * Date: 11/12/14 Time: 7:10 PM
 */
@Aspect
@Component
public class OptimisticLockingExecutor implements Ordered {

  private static final Logger logger = LoggerFactory.getLogger(OptimisticLockingExecutor.class);
  private final int maxRetries;
  private final int order;

  public OptimisticLockingExecutor(
      @Value("${optimisticlocking.retries:5}") final int maxRetries,
      @Value("${optimisticlocking.order:1}") final int order) {
    this.maxRetries = maxRetries;
    this.order = order;
  }

  public int getOrder() {
    return this.order;
  }

  @Around("com.jtbdevelopment.games.rest.aop.RestSystemArchitecture.inRestServices()")
  public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
    int numAttempts = 0;
    OptimisticLockingFailureException lockFailureException = null;
    while (numAttempts <= this.maxRetries) {
      numAttempts++;
      try {
        return pjp.proceed();
      } catch (OptimisticLockingFailureException ex) {
        logger.info("Optimistic Lock Exception Detected");
        lockFailureException = ex;
      }

    }

    logger.warn("Optimistic Lock Exception Retries Exhausted");
    //noinspection ConstantConditions
    throw lockFailureException;
  }
}
