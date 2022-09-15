package com.bank.bankaccountapi.controller;

import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;
import com.bank.bankaccountapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("status/{accountNumber}")
    public String checkAccountStatus(@PathVariable("accountNumber") String accountNumber) {
        log.info("Checking account status. accountNumber = {}", accountNumber);
        String result = accountService.getAccountStatusByAccountNumber(accountNumber);
        log.info("Checked account status. accountNumber = {}; status = {}", accountNumber, result);
        return result;
    }

    @GetMapping("balance/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountBalanceInquiry(@PathVariable("accountNumber") String accountNumber) {
        log.info("Getting account balance inquiry. accountNumber = {}", accountNumber);
        AccountDto result = accountService.getAccountBalanceInquiryByAccountNumber(accountNumber);
        log.info("Account balance inquiry = {}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("operation")
    public ResponseEntity<AccountDto> applyAccountOperation(@RequestBody @Valid AccountOperationDto accountOperation) {
        log.info("Account operation was started. accountOperation = {}", accountOperation);
        AccountDto accountDto = accountService.applyAccountOperation(accountOperation);
        log.info("Account operation was ended. accountOperation = {}; accountDto = {}", accountOperation, accountDto);
        return ResponseEntity.ok(accountDto);
    }

}
