package zerobase.accounthw.controller;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.dto.AccountDto;
import zerobase.accounthw.dto.CheckAccount;
import zerobase.accounthw.dto.CreateAccount;
import zerobase.accounthw.dto.DeleteAccount;
import zerobase.accounthw.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<CreateAccount.Response> createAccount(
            @RequestBody @Valid CreateAccount.Request request) {

        AccountDto accountDto = accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance());

        return ResponseEntity.ok(
                CreateAccount.Response.from(accountDto));
    }

    @DeleteMapping("/account")
    public ResponseEntity<DeleteAccount.Response> deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request) {

        AccountDto accountDto = accountService.deleteAccount(
                request.getUserId(),
                request.getAccountNumber()
        );

        return ResponseEntity.ok(DeleteAccount.Response.from(accountDto));
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<List<CheckAccount.Response>> getAccount(@PathVariable long id) {

        List<AccountDto> accountDtos = accountService.getAccounts(id);


        return ResponseEntity.ok(
                accountDtos.stream()
                        .map(CheckAccount.Response::from)
                        .collect(Collectors.toList()));
    }
}
