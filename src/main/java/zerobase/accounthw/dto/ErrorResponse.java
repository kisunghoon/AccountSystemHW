package zerobase.accounthw.dto;

import lombok.*;
import zerobase.accounthw.type.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;
}
