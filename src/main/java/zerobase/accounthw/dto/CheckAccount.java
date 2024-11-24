package zerobase.accounthw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import zerobase.accounthw.domain.Account;

public class CheckAccount {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private Long initialBalance;

        public static Response from(AccountDto accountDto) {

            return Response.builder()
                    .accountNumber(accountDto.getAccountNumber())
                    .initialBalance(accountDto.getBalance())
                    .build();
        }
    }



}
