package zerobase.accounthw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class DeleteAccount {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Request{

        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{

        private Long userid;
        private String accountnumber;
        private LocalDateTime unRegisteredAt;

        public static Response from(AccountDto accountDto){

            return Response.builder()
                    .userid(accountDto.getUserId())
                    .accountnumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
