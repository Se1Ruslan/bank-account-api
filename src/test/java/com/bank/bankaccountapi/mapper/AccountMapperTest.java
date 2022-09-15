package com.bank.bankaccountapi.mapper;

import com.bank.bankaccountapi.domain.Account;
import com.bank.bankaccountapi.domain.AccountStatus;
import com.bank.bankaccountapi.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
class AccountMapperTest {

    private final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);


    @Test
    void shouldMapAccountToAccountDto() {
        //given
        Account input = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        AccountDto expected = AccountDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        //when
        AccountDto actual = mapper.map(input);
        log.info("actual = {}", actual);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void shouldMapAccountDtoToAccount() {
        //given
        Account expected = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        AccountDto input = AccountDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        //when
        Account actual = mapper.map(input);
        log.info("actual = {}", actual);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void shouldMapAccountListToAccountDtoList() {
        //given
        Account account = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();
        List<Account> input = List.of(account);

        AccountDto accountDto = AccountDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();
        List<AccountDto> expected = List.of(accountDto);


        //when
        List<AccountDto> actual = mapper.mapToAccountDtoList(input);
        log.info("actual = {}", actual);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void shouldMapAccountDtoListToAccountList() {
        //given
        Account account = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();
        List<Account> expected = List.of(account);

        AccountDto accountDto = AccountDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();
        List<AccountDto> input = List.of(accountDto);


        //when
        List<Account> actual = mapper.mapToAccountList(input);
        log.info("actual = {}", actual);

        //then
        assertEquals(expected, actual);
    }
}