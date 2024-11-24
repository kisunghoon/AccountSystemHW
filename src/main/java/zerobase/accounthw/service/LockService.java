package zerobase.accounthw.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.type.ErrorCode;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LockService {

    private final RedissonClient redissonClient;

    public String lock(String accountNumber){

        RLock lock = redissonClient.getLock(getLockkey(accountNumber));

        try{
            boolean isLock = lock.tryLock(1,15, TimeUnit.SECONDS);
            if(!isLock){
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }

        } catch(AccountException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "get Lock Success";

    }

    public void unlock(String accountNumber){

        redissonClient.getLock(getLockkey(accountNumber)).unlock();
    }

    private static String getLockkey(String accountNumber){
        return "ACLK:" + accountNumber;
    }
}
