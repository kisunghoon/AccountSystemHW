package zerobase.accounthw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.domain.AccountUser;
import zerobase.accounthw.dto.AccountDto;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.repository.AccountRepository;
import zerobase.accounthw.repository.AccountUserRepository;
import zerobase.accounthw.type.AccountStatus;
import zerobase.accounthw.type.ErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountUserRepository accountUserRepository;



    @Test
    void createAccountSuccess() {
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000012").build()));

        given(accountRepository.save(any()))
                .willReturn((Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L,1000L);

        verify(accountRepository , times(1)).save(captor.capture());
        assertEquals(12L,accountDto.getUserId());
        assertEquals("1000000012",captor.getValue().getAccountNumber());

    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class ,
                () -> accountService.createAccount(1L,1000L));

        assertEquals(ErrorCode.USER_NOT_FOUND,exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 당 최대 계죄는 10개")
    void createAccount_maxAccountIs10(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception = assertThrows(AccountException.class ,
                () -> accountService.createAccount(1L,1000L));

        assertEquals(ErrorCode.MAX_ACCOUNT_USER_10,exception.getErrorCode());
    }

    @Test
    @DisplayName("중복 계좌 번호 생성 방지 - 성공")
    void createAccount_AvoidDuplicateAccountNumber_Success() {
        // given
        AccountUser user = AccountUser.builder()
                .id(1L)
                .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // 최신 계좌 번호 설정
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));

        // 중복 검사
        given(accountRepository.countByAccountNumber(anyString()))
                .willReturn(1)
                .willReturn(0);

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("3626991755").build());

        // when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        // then
        verify(accountRepository, times(2)).countByAccountNumber(anyString()); // 중복 확인 2번 호출
        assertEquals("3626991755", accountDto.getAccountNumber());
    }

    @Test
    void deleteAccountSuccess() {
        AccountUser user = AccountUser.builder().name("Pobi").build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.deleteAccount(1L,"1000000012");

        verify(accountRepository,times(1)).save(captor.capture());
        assertEquals(AccountStatus.UNREGISTERED,captor.getValue().getAccountStatus());

    }

    @Test
    @DisplayName("계좌 소유주가 다름")
    void deleteAccountFailed_userNotMatch(){
        AccountUser Pobi = AccountUser.builder().name("Pobi").build();
        Pobi.setId(12L);

        AccountUser Harry = AccountUser.builder().name("Harry").build();
        Harry.setId(13L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().accountUser(Harry)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        AccountException exception = assertThrows(AccountException.class ,
                () -> accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH,exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_balanceNotEmpty(){
        AccountUser Pobi = AccountUser.builder().name("Pobi").build();
        Pobi.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi).balance(10L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class ,
                () -> accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.BALANCE_NOT_EMPTY , exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 해지할 수 없다")
    void deleteAccountFailed_alreadyUnregistered(){
        AccountUser Pobi = AccountUser.builder().name("Pobi").build();
        Pobi.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        AccountException exception = assertThrows(AccountException.class ,
                () -> accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED , exception.getErrorCode());
    }
}