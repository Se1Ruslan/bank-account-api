package com.bank.bankaccountapi.service;

import com.bank.bankaccountapi.domain.Account;
import com.bank.bankaccountapi.domain.AccountStatus;
import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;
import com.bank.bankaccountapi.dto.OperationSign;
import com.bank.bankaccountapi.expection.AccountApiException;
import com.bank.bankaccountapi.mapper.AccountMapper;
import com.bank.bankaccountapi.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceImplTest {

    private static final String ACCOUNT_NUMBER_1 = "test-1";
    private static final String ACCOUNT_NUMBER_2 = "test-2";
    private static final Long CURRENCY_1 = 978L;
    private static final Long CURRENCY_2 = 1000L;
    private static final Double BALANCE = 20.0;
    private static final Account account1 = Account.builder()
            .accountNumber(ACCOUNT_NUMBER_1)
            .currency(CURRENCY_1)
            .balance(BALANCE)
            .status(AccountStatus.OPEN)
            .build();

    private static final Account account2 = Account.builder()
            .accountNumber(ACCOUNT_NUMBER_2)
            .currency(CURRENCY_1)
            .balance(BALANCE)
            .status(AccountStatus.CLOSED)
            .build();
    private static final AccountDto accountDto1 = AccountDto.builder()
            .accountNumber(ACCOUNT_NUMBER_1)
            .currency(CURRENCY_1)
            .balance(BALANCE)
            .status(AccountStatus.OPEN)
            .build();

    @Mock
    private AccountRepo accountRepo;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldReturnAccountStatusByAccountNumber() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));

        //when
        String actual = accountService.getAccountStatusByAccountNumber(ACCOUNT_NUMBER_1);
        log.info("actual = {}", actual);

        //then
        assertEquals("OPEN", actual);
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
    }

    @Test()
    void shouldNotReturnAccountStatusByWrongAccountNumber() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.empty());

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.getAccountStatusByAccountNumber(ACCOUNT_NUMBER_1));
        log.error("Handled test-error", thrown);

        //then
        assertEquals("Account does not exists.AccountNumber = test-1", thrown.getMessage());
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
    }

    @Test
    void shouldReturnAccountBalanceInquiryByAccountNumber() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));
        when(accountMapper.map(account1))
                .thenReturn(accountDto1);

        //when
        AccountDto actual = accountService.getAccountBalanceInquiryByAccountNumber(ACCOUNT_NUMBER_1);
        log.info("actual = {}", actual);

        //then
        assertEquals(accountDto1, actual);
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
    }

    @Test
    void shouldNotReturnAccountBalanceInquiryByWrongAccountNumber() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.empty());
        when(accountMapper.map(account1))
                .thenReturn(accountDto1);

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.getAccountBalanceInquiryByAccountNumber(ACCOUNT_NUMBER_1));
        log.error("Handled test-error", thrown);

        //then
        assertEquals("Account does not exists.AccountNumber = test-1", thrown.getMessage());
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
    }

    @Test
    void shouldApplyDebitAccountOperation() {
        //given
        Account updatedAccount = account1.toBuilder().balance(BALANCE + 10.0).build();
        AccountDto updatedAccountDto = accountDto1.toBuilder().balance(BALANCE + 10.0).build();
        when(accountMapper.map(updatedAccount))
                .thenReturn(updatedAccountDto);
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));
        when(accountRepo.save(updatedAccount))
                .thenReturn(updatedAccount);
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .currency(CURRENCY_1)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();

        //when
        AccountDto actual = accountService.applyAccountOperation(operation);
        log.info("actual = {}", actual);

        //then
        assertEquals(updatedAccountDto, actual);
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
        verify(accountRepo, times(1)).save(any());
    }

    @Test
    void shouldApplyCreditAccountOperation() {
        //given
        Account updatedAccount = account1.toBuilder().balance(BALANCE - 10.0).build();
        AccountDto updatedAccountDto = accountDto1.toBuilder().balance(BALANCE - 10.0).build();
        when(accountMapper.map(updatedAccount))
                .thenReturn(updatedAccountDto);
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));
        when(accountRepo.save(updatedAccount))
                .thenReturn(updatedAccount);
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .currency(CURRENCY_1)
                .amount(10.0)
                .operationSign(OperationSign.CREDIT)
                .build();

        //when
        AccountDto actual = accountService.applyAccountOperation(operation);
        log.info("actual = {}", actual);

        //then
        assertEquals(updatedAccountDto, actual);
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
        verify(accountRepo, times(1)).save(any());
    }

    @Test
    void shouldNotApplyOperationForNonExistingAccount() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.empty());
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .currency(CURRENCY_1)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.applyAccountOperation(operation));
        log.error("Handled test-error", thrown);

        //then
        assertTrue(thrown.getMessage().contains("Account does not exists"));
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
        verify(accountRepo, times(0)).save(any());
    }

    @Test
    void shouldNotApplyOperationForNotOpenedAccount(){
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_2))
                .thenReturn(Optional.of(account2));
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_2)
                .currency(CURRENCY_1)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.applyAccountOperation(operation));
        log.error("Handled test-error", thrown);

        //then
        assertTrue(thrown.getMessage().contains("Account is not OPEN"));
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_2);
        verify(accountRepo, times(0)).save(any());
    }

    @Test
    void shouldNotApplyOperationWithWrongCurrency() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .currency(CURRENCY_2)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.applyAccountOperation(operation));
        log.error("Handled test-error", thrown);

        //then
        assertTrue(thrown.getMessage().contains("Account and operation currencies are different"));
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
        verify(accountRepo, times(0)).save(any());
    }

    @Test
    void shouldNotApplyCreditOperationIfAccountDoesNotHaveEnoughBalance() {
        //given
        when(accountRepo.findById(ACCOUNT_NUMBER_1))
                .thenReturn(Optional.of(account1));
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .currency(CURRENCY_1)
                .amount(BALANCE + 10.0)
                .operationSign(OperationSign.CREDIT)
                .build();

        //when
        AccountApiException thrown = assertThrows(AccountApiException.class,
                () -> accountService.applyAccountOperation(operation));
        log.error("Handled test-error", thrown);

        //then
        assertTrue(thrown.getMessage().contains("Account does not have enough balance to process operation"));
        verify(accountRepo, times(1))
                .findById(ACCOUNT_NUMBER_1);
        verify(accountRepo, times(0)).save(any());
    }
}