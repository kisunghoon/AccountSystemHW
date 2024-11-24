package zerobase.accounthw.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.accounthw.dto.UseBalance;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.type.ErrorCode;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {

        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,"1234567890",1000L);

        lockAopAspect.aroundMethod(proceedingJoinPoint,request);

        verify(lockService,times(1)).lock(lockCaptor.capture());
        verify(lockService,times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234567890",lockCaptor.getValue());
        assertEquals("1234567890",unlockCaptor.getValue());
    }

    @Test
    void lockAndUnlockTrow() throws Throwable {

        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,"1234567890",1000L);

        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        assertThrows(AccountException.class , ()-> lockAopAspect.aroundMethod(proceedingJoinPoint,request));

        verify(lockService,times(1)).lock(lockCaptor.capture());
        verify(lockService,times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234567890",lockCaptor.getValue());
        assertEquals("1234567890",unlockCaptor.getValue());
    }

}