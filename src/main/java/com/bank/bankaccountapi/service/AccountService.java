package com.bank.bankaccountapi.service;

import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface AccountService {
    String getAccountStatusByAccountNumber(@NotEmpty String accountNumber);
    AccountDto getAccountBalanceInquiryByAccountNumber(@NotEmpty String accountNumber);
    AccountDto applyAccountOperation(@NotNull AccountOperationDto accountOperation);
}
