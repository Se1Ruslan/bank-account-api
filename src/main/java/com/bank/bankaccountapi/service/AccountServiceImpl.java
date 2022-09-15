package com.bank.bankaccountapi.service;

import com.bank.bankaccountapi.domain.Account;
import com.bank.bankaccountapi.domain.AccountStatus;
import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;
import com.bank.bankaccountapi.expection.AccountApiException;
import com.bank.bankaccountapi.mapper.AccountMapper;
import com.bank.bankaccountapi.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final AccountMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public String getAccountStatusByAccountNumber(String accountNumber) {
        log.info("Getting account status by account number. accountNumber = {}", accountNumber);
        return accountRepo.findById(accountNumber)
                .map(account -> account.getStatus().toString())
                .orElseThrow(
                        () -> new AccountApiException("Account does not exists.AccountNumber = " + accountNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountBalanceInquiryByAccountNumber(String accountNumber) {
        log.info("Getting account balance inquiry by account number. accountNumber = {}", accountNumber);
        return accountRepo.findById(accountNumber)
                .map(mapper::map)
                .orElseThrow(
                        () -> new AccountApiException("Account does not exists.AccountNumber = " + accountNumber));
    }

    @Override
    @Transactional
    public AccountDto applyAccountOperation(AccountOperationDto operation) {
        log.info("Operation was started. operation = {}", operation);
        Account account = accountRepo.findById(operation.getAccountNumber())
                .orElseThrow(
                        () -> new AccountApiException("Account does not exists. AccountNumber = "
                                + operation.getAccountNumber()));

        validateAccountStatus(account);
        validateCurrency(operation, account);

        processAccountOperation(operation, account);

        Account updatedAccount = accountRepo.save(account);

        log.info("Operation was ended successfully. operation = {}; updatedAccount = {}", operation, updatedAccount);
        return mapper.map(updatedAccount);
    }

    private void processAccountOperation(AccountOperationDto operation, Account account) {
        switch (operation.getOperationSign()) {
            case DEBIT:
                account.setBalance(account.getBalance() + operation.getAmount());
                break;
            case CREDIT:
                if (account.getBalance() < operation.getAmount()) {
                    throw new AccountApiException("Account does not have enough balance to process operation;" +
                            "account = " + account + ";"
                            + "operation = " + operation);
                }
                account.setBalance(account.getBalance() - operation.getAmount());
                break;
            default:
                throw new AccountApiException("Not valid Operation Sign");
        }
    }

    private void validateAccountStatus(Account account) {
        if (!AccountStatus.OPEN.equals(account.getStatus())) {
            throw new AccountApiException("Account is not OPEN. account = " + account);
        }
    }

    private void validateCurrency(AccountOperationDto accountOperation, Account account) {
        if (!accountOperation.getCurrency().equals(account.getCurrency())) {
            throw new AccountApiException("Account and operation currencies are different. " +
                    "account's currency = " + account.getCurrency() + ";" +
                    "operation's currency = " + accountOperation.getCurrency());
        }
    }
}
