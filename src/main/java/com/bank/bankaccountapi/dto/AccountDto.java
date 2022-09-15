package com.bank.bankaccountapi.dto;

import com.bank.bankaccountapi.domain.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotEmpty(message = "accountNumber must be not empty")
    private String accountNumber;

    @NotNull(message = "currency must be not null")
    private Long currency;

    @NotNull(message = "balance must be not null")
    @Positive(message = "balance must be positive number")
    private Double balance;

    @NotNull(message = "account status must be not null")
    private AccountStatus status;
}
