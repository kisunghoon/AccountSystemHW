package zerobase.accounthw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.domain.AccountUser;
import zerobase.accounthw.domain.Transaction;
import zerobase.accounthw.dto.TransactionDto;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.repository.AccountRepository;
import zerobase.accounthw.repository.AccountUserRepository;
import zerobase.accounthw.repository.TransactionRepository;
import zerobase.accounthw.type.AccountStatus;
import zerobase.accounthw.type.ErrorCode;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static zerobase.accounthw.type.TransactionResultType.F;
import static zerobase.accounthw.type.TransactionResultType.S;
import static zerobase.accounthw.type.TransactionType.CANCEL;
import static zerobase.accounthw.type.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionDto useBalance(Long userId, String accountNumber,
                                     Long amount) {

        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUseBalance(user,account,amount);

        account.useBalance(amount);

        Transaction transaction = transactionRepository.save(
                                        Transaction.builder()
                                                .transactionType(USE)
                                                .transactionResultType(S)
                                                .account(account)
                                                .amount(amount)
                                                .balanceSnapshot(account.getBalance())
                                                .transactionId(UUID.randomUUID().toString().replace("-", ""))
                                                .transactedAt(LocalDateTime.now())
                                                .build());


        return TransactionDto.fromEntity(transaction);
    }


    private void validateUseBalance(AccountUser user, Account account , Long amount){

        if(!Objects.equals(account.getAccountUser().getId(), user.getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_NOT_MATCH);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if(amount < 10){
            throw new AccountException(ErrorCode.AMOUNT_TOO_SMALL);
        }

        if(amount > 100000000){
            throw new AccountException(ErrorCode.AMOUNT_TOO_BIG);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionDto cancelBalance(String transactionId,
                                        String accountNumber,  Long amount) {

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction,account,amount);

        account.cancelBalance(amount);

        Transaction canceltransaction = transactionRepository.save(
                Transaction.builder()
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build());

        return TransactionDto.fromEntity(canceltransaction);

    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {

        if(transaction.getAmount().longValue() != amount.longValue()){
            throw new AccountException(ErrorCode.AMOUNT_NOT_MATCH);
        }

        if(!Objects.equals(transaction.getAccount().getId(), account.getId())){
            throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
        }


    }

    public TransactionDto getTransaction(String transactionId) {

        return TransactionDto.fromEntity(
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTIONID_NOT_FOUND))
        );
    }

    public void failSaveTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        transactionRepository.save(
                Transaction.builder()
                        .transactionType(USE)
                        .transactionResultType(F)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build());

    }
}
