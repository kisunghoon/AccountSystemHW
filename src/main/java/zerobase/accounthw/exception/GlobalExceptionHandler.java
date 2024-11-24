package zerobase.accounthw.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.accounthw.dto.ErrorResponse;
import zerobase.accounthw.type.ErrorCode;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} is occurred. ", e.getMessage());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolateException(DataIntegrityViolationException  e) {
        log.error("{} is occurred. ", e.getMessage());

        return new ErrorResponse(ErrorCode.DATABASE_ERROR, "데이터베이스 무결성 위반");
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneralException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);

        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다.");
    }

}
