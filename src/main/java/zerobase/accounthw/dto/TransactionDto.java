package zerobase.accounthw.dto;

import lombok.*;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.domain.Transaction;
import zerobase.accounthw.type.TransactionResultType;
import zerobase.accounthw.type.TransactionType;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String accountNumber;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private Account account;
    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
