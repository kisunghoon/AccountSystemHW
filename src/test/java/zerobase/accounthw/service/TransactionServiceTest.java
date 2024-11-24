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
import zerobase.accounthw.domain.Transaction;
import zerobase.accounthw.dto.TransactionDto;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.repository.AccountRepository;
import zerobase.accounthw.repository.AccountUserRepository;
import zerobase.accounthw.repository.TransactionRepository;
import zerobase.accounthw.type.AccountStatus;
import zerobase.accounthw.type.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static zerobase.accounthw.type.TransactionResultType.F;
import static zerobase.accounthw.type.TransactionResultType.S;
import static zerobase.accounthw.type.TransactionType.CANCEL;
import static zerobase.accounthw.type.TransactionType.USE;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void successUseBalance(){

        AccountUser user = AccountUser.builder().name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .accountNumber("1000000012")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        TransactionDto transactionDto = transactionService.useBalance(12L,"1000000000",1000L);

        verify(transactionRepository,times(1)).save(captor.capture());

        assertEquals(1000L,captor.getValue().getAmount());

        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(USE, transactionDto.getTransactionType());
        assertEquals(9000L,transactionDto.getBalanceSnapshot());
        assertEquals(1000L,transactionDto.getAmount());



    }


    @Test
    void successCancelBalance(){
        AccountUser user = AccountUser.builder().name("Pobi").build();
        user.setId(12L);

        Account acount = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(acount)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .amount(1000L)
                .balanceSnapshot(9000L)
                .transactedAt(LocalDateTime.now())
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(acount));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(acount)
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        TransactionDto transactionDto = transactionService.cancelBalance("transactionId","1000000012",1000L);

        verify(transactionRepository , times(1)).save(captor.capture());
        assertEquals(1000L,captor.getValue().getAmount());

        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(CANCEL, transactionDto.getTransactionType());
        assertEquals(9000L,transactionDto.getBalanceSnapshot());
        assertEquals(1000L,transactionDto.getAmount());
    }

    @Test
    void getTransaction(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();

        user.setId(12L);
        Account acount = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(acount)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .amount(1000L)
                .balanceSnapshot(9000L)
                .transactedAt(LocalDateTime.now())
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        TransactionDto transactionDto = transactionService.getTransaction("transactionId");

        assertEquals(USE,transactionDto.getTransactionType());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(1000L,transactionDto.getAmount());
        assertEquals("transactionId",transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("트랜잭션 실패 저장  성공 ")
    void failSaveTransaction(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account acount = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012")
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(acount));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(acount)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.failSaveTransaction("1000000000", 1000L);

        verify(transactionRepository , times(1)).save(captor.capture());
        assertEquals(1000L,captor.getValue().getAmount());
        assertEquals(10000L,captor.getValue().getBalanceSnapshot());
        assertEquals(F,captor.getValue().getTransactionResultType());


    }

    @Test
    @DisplayName("사용자와 계좌의 소유주가 다릅니다.")
    void deleteAccountFailed_userUnMatch() {
        AccountUser Pobi = AccountUser.builder()

                .name("Pobi").build();
        Pobi.setId(12L);
        AccountUser Harry = AccountUser.builder()
                .name("Harry").build();
        Pobi.setId(13L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Harry).balance(0L)
                        .accountNumber("1000000012").build()));


        AccountException exception = assertThrows(AccountException.class ,
                () -> transactionService.useBalance(1L,"1234567890",1000L));

        assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH,exception.getErrorCode());
    }


    @Test
    @DisplayName("계좌가 이미 해지되었습니다")
    void deleteAccountFailed_aleradyUnregistered() {
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(10L)
                        .accountNumber("1000000012")
                        .build()));


        AccountException exception = assertThrows(AccountException.class ,
                () -> transactionService.useBalance(1L,"1234567890",1000L));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED,exception.getErrorCode());
    }


    @Test
    @DisplayName("거래 금액이 계좌 잔액보다 큽니다.")
    void excceedUseBalance(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account acount = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(100L)
                .accountNumber("1000000012")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(acount));


        AccountException exception = assertThrows(AccountException.class ,
                () -> transactionService.useBalance(1L,"1234567890",1000L));

        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE,exception.getErrorCode());

    }

    @Test
    @DisplayName("거래 금액이 너무 작습니다.")
    void amountTooSmall(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account acount = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1L)
                .accountNumber("1000000012")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(acount));


        AccountException exception = assertThrows(AccountException.class ,
                () -> transactionService.useBalance(1L,"1234567890",1L));

        assertEquals(ErrorCode.AMOUNT_TOO_SMALL,exception.getErrorCode());

    }

    @Test
    @DisplayName("거래 금액이 너무 큽니다.")
    void amountTooBig(){

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(100000000L)
                .accountNumber("1000000012")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1234567890", 100000001L));

        assertEquals(ErrorCode.AMOUNT_TOO_BIG, exception.getErrorCode());

    }
}