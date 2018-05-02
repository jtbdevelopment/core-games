package com.jtbdevelopment.games.rest.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Date: 11/15/2014 Time: 10:46 AM
 */
public class OptimisticLockingExecutorTest {

  private OptimisticLockingExecutor retry = new OptimisticLockingExecutor(2, 2);

  @Test
  public void testMethodAnnotations() throws NoSuchMethodException {
    Assert.assertEquals("com.jtbdevelopment.games.rest.aop.RestSystemArchitecture.inRestServices()",
        OptimisticLockingExecutor.class
            .getMethod("doConcurrentOperation", new Class[]{ProceedingJoinPoint.class})
            .getAnnotation(Around.class).value());
  }

  @Test
  public void testOrder() {
    Assert.assertEquals(2, retry.getOrder());
  }

  @Test
  public void testCallsFine() throws Throwable {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Object retVal = new Object();
    Mockito.when(joinPoint.proceed()).thenReturn(retVal);
    Assert.assertSame(retVal, retry.doConcurrentOperation(joinPoint));

    Mockito.verify(joinPoint, Mockito.times(1)).proceed();
  }

  @Test
  public void testRetriesOnce() throws Throwable {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Object retVal = new Object();
    Mockito.when(joinPoint.proceed()).thenThrow(new OptimisticLockingFailureException("X"))
        .thenReturn(retVal);
    Assert.assertSame(retVal, retry.doConcurrentOperation(joinPoint));

    Mockito.verify(joinPoint, Mockito.times(2)).proceed();
  }

  @Test
  public void testRetriesTwice() throws Throwable {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Object retVal = new Object();
    Mockito.when(joinPoint.proceed()).thenThrow(new OptimisticLockingFailureException("X"))
        .thenThrow(new OptimisticLockingFailureException("X")).thenReturn(retVal);
    Assert.assertSame(retVal, retry.doConcurrentOperation(joinPoint));

    Mockito.verify(joinPoint, Mockito.times(3)).proceed();
  }

  @Test(expected = OptimisticLockingFailureException.class)
  public void testFailsAfterMax() throws Throwable {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Mockito.when(joinPoint.proceed()).thenThrow(new OptimisticLockingFailureException("X"))
        .thenThrow(new OptimisticLockingFailureException("X"))
        .thenThrow(new OptimisticLockingFailureException("X"));
    try {
      retry.doConcurrentOperation(joinPoint);
    } finally {
      Mockito.verify(joinPoint, Mockito.times(3)).proceed();
    }

  }

  @Test(expected = RuntimeException.class)
  public void testFailsOnFirstOtherException() throws Throwable {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Mockito.when(joinPoint.proceed()).thenThrow(new RuntimeException("X"));
    try {
      retry.doConcurrentOperation(joinPoint);
    } finally {
      Mockito.verify(joinPoint, Mockito.times(1)).proceed();
    }

  }
}
