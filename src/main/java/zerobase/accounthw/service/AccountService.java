package zerobase.accounthw.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.domain.AccountUser;
import zerobase.accounthw.dto.AccountDto;
import zerobase.accounthw.dto.CheckAccount;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.repository.AccountRepository;
import zerobase.accounthw.repository.AccountUserRepository;
import zerobase.accounthw.type.AccountStatus;
import zerobase.accounthw.type.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static zerobase.accounthw.type.AccountStatus.IN_USE;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId,long initialBalance) {


        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);


        //응용하는 방식으로 랜덤 숫자 10자리로 구성하는 방향으로 선택한 코드입니다.
        String accountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(Account::getAccountNumber)
                .orElse(generateRandomAccountNumber());

        //중복 체크
        while(accountRepository.countByAccountNumber(accountNumber) >= 1){
            accountNumber = generateRandomAccountNumber();
        }

        //계좌 저장
        Account account = accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(accountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build());

        return AccountDto.fromEntity(account);
    }

    private AccountUser getAccountUser(Long userId) {

        Optional<AccountUser> optionalAccountUser = accountUserRepository.findById(userId);

        if (!optionalAccountUser.isPresent()) {
            throw new AccountException(ErrorCode.USER_NOT_FOUND);
        }
        AccountUser accountUser = optionalAccountUser.get();

        return accountUser;
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_USER_10);
        }
    }


    private String generateRandomAccountNumber() {
        Random random = new Random();

        StringBuilder accountNumber = new StringBuilder();
        for(int i=0;i<10;i++){
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }


    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {

        //사용자 검증
        AccountUser accountUser = getAccountUser(userId);

        //계좌 찾기
        Account account = findAccount(accountNumber);

        //계좌 삭제 검증
        validateDeleteAccount(accountUser,account);

        //해지
        updateAccountUnregist(account);

        return AccountDto.fromEntity(account);
    }

    private void updateAccountUnregist(Account account) {
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {

        if(!Objects.equals(accountUser.getId() , account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_NOT_MATCH);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }

    }


    public List<AccountDto> getAccounts(long id) {
        AccountUser accountUser = getAccountUser(id);

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return accounts.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
    }
}
