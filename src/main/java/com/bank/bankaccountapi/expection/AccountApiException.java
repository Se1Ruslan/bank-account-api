package com.bank.bankaccountapi.expection;

public class AccountApiException extends RuntimeException {

    public AccountApiException(String message) {
        super(message);
    }
}
