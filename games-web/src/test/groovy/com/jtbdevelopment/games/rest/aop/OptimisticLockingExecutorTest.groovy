package com.jtbdevelopment.games.rest.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.OptimisticLockingFailureException

/**
 * Date: 11/15/2014
 * Time: 10:46 AM
 */
class OptimisticLockingExecutorTest extends GroovyTestCase {
    OptimisticLockingExecutor retry = new OptimisticLockingExecutor(maxRetries: 2)

    void testMethodAnnotations() {
        assert OptimisticLockingExecutor.class.
                getMethod('doConcurrentOperation', [ProceedingJoinPoint.class] as Class[]).
                getAnnotation(Around.class).
                value() == 'com.jtbdevelopment.games.rest.aop.RestSystemArchitecture.inRestServices()'
    }

    void testValueAnnotations() {
        assert OptimisticLockingExecutor.class.
                getDeclaredField('maxRetries')?.
                getAnnotation(Value.class)?.
                value() == '${optimisticlocking.retries:5}'
        assert OptimisticLockingExecutor.class.
                getDeclaredField('order')?.
                getAnnotation(Value.class)?.
                value() == '${optimisticlocking.order:1}'
    }

    public void testOrder() {
        retry.order = 2
        assert retry.getOrder() == retry.order
    }

    public void testCallsFine() {
        int count = 0
        Object retVal = new Object();

        assert retVal.is(retry.doConcurrentOperation([proceed: { ++count; retVal }] as ProceedingJoinPoint))
        assert count == 1
    }

    public void testRetriesOnce() {
        int count = 0
        Object retVal = new Object();

        assert retVal.is(retry.doConcurrentOperation([proceed: {
            ++count;
            if (count == 1) throw new OptimisticLockingFailureException("msg")
            retVal
        }] as ProceedingJoinPoint))
        assert count == 2
    }

    public void testRetriesTwice() {
        int count = 0
        Object retVal = new Object();

        assert retVal.is(retry.doConcurrentOperation([proceed: {
            ++count;
            if (count < 3) throw new OptimisticLockingFailureException("msg")
            retVal
        }] as ProceedingJoinPoint))
        assert count == 3
    }

    public void testFailsAfterMax() {
        int count = 0

        try {
            retry.doConcurrentOperation([proceed: {
                ++count;
                throw new OptimisticLockingFailureException("msg")
            }] as ProceedingJoinPoint)
            fail()
        } catch (OptimisticLockingFailureException e) {
            assert e.message == "msg"
        }
        assert count == 3
    }

    public void testFailsOnFirstOtherException() {
        int count = 0

        try {
            retry.doConcurrentOperation([proceed: {
                ++count;
                throw new RuntimeException("msg")
            }] as ProceedingJoinPoint)
            fail()
        } catch (RuntimeException e) {
            assert e.message == "msg"
        }
        assert count == 1
    }
}
