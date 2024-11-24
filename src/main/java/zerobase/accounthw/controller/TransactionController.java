package zerobase.accounthw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.accounthw.aop.AccountLock;
import zerobase.accounthw.dto.CancelBalance;
import zerobase.accounthw.dto.CheckTransactionResponse;
import zerobase.accounthw.dto.UseBalance;
import zerobase.accounthw.exception.AccountException;
import zerobase.accounthw.service.TransactionService;

@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public ResponseEntity<UseBalance.Response> useBalance
            (@Valid @RequestBody UseBalance.Request request) {


        try{
            Thread.sleep(3000L);
            return ResponseEntity.ok(UseBalance.Response.from(

                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(),request.getAmount()))
            );

        } catch(AccountException e){

            transactionService.failSaveTransaction(request.getAccountNumber(),request.getAmount());

            throw  e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public ResponseEntity<CancelBalance.Response> cancelBalance(
            @Valid @RequestBody CancelBalance.Request request){


        try{
            return ResponseEntity.ok(CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount()))
            );
        }catch(AccountException e){
            transactionService.failSaveTransaction(request.getAccountNumber(),request.getAmount());

            throw  e;
        }
    }


    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<CheckTransactionResponse> getTransactionResponse(
            @PathVariable String transactionId) {


        return ResponseEntity.ok(CheckTransactionResponse.from(
                transactionService.getTransaction(transactionId))
        );
    }




}
