package zerobase.accounthw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import zerobase.accounthw.aop.AccountLockIdInterface;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(zerobase.accounthw.aop.AccountLock) && args(request)")
    public Object aroundMethod(ProceedingJoinPoint pjp, AccountLockIdInterface request)
            throws Throwable {

        //lock 취득 시도
        String accountNumber = request.getAccountNumber();
        log.info("Attempting to lock for accountNumber: {}", accountNumber);

        lockService.lock(accountNumber);
        try {
            // 실제 메서드 실행
            return pjp.proceed();

        } finally {
            // Lock 해제
            try {
                lockService.unlock(accountNumber);
                log.info("Unlocked for accountNumber: {}", accountNumber);
            } catch (Exception e) {
                log.error("Failed to unlock for accountNumber: {}", accountNumber, e);
            }
        }
    }
}
