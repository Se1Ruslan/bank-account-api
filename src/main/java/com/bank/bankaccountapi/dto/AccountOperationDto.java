package com.bank.bankaccountapi.dto;

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
public class AccountOperationDto {
    @NotEmpty(message = "accountNumber must be not empty")
    private String accountNumber;

    @NotNull(message = "currency must be not null")
    private Long currency;

    @NotNull(message = "amount must be not null")
    @Positive(message = "amount must be positive number")
    private Double amount;

    @NotNull(message = "operationSign must be not null")
    private OperationSign operationSign;
}
