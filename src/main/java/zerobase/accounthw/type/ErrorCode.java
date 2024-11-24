package zerobase.accounthw.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_ACCOUNT_USER_10("사용자가 최대 계좌는 10개입니다."),
    ACCOUNT_NOT_FOUND("계좌가 확인되지 않습니다."),
    TRANSACTIONID_NOT_FOUND("트랜잭션이 해당 계좌의 거래가 아닙니다."),
    USER_ACCOUNT_NOT_MATCH("사용자와 계좌의 소유주가 다릅니다. "),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다. "),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액보다 큽니다."),
    AMOUNT_TOO_BIG("거래 금액이 너무 큽니다."),
    AMOUNT_TOO_SMALL("거래 금액이 너무 작습니다."),
    TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
    DATABASE_ERROR("디비 무결성 에러"),
    INTERNAL_SERVER_ERROR("내부 서버 에러가 생겼습니다."),
    AMOUNT_NOT_MATCH("원거래 금액과 취소 금액이 다른 경우"),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중입니다.");

    private final String description;
}
