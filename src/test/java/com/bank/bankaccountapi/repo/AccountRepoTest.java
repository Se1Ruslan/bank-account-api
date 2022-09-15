package com.bank.bankaccountapi.repo;

import com.bank.bankaccountapi.domain.Account;
import com.bank.bankaccountapi.domain.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
class AccountRepoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    AccountRepo accountRepo;

    @Test
    void shouldReturnAllAccounts() {
        //when
        List<Account> actual = accountRepo.findAll();
        log.info("actual = {}", actual);

        //then
        assertNotNull(actual);
        assertEquals(4, actual.size());
    }


    @Test
    void shouldFindByAccountNumber() {
        //given
        Account expected = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        //when
        Optional<Account> actual = accountRepo.findById("test-1");
        log.info("actual = {}", actual);

        //then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void shouldUpdateAccount() {
        //given
        Account expected = Account.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(20.00)
                .status(AccountStatus.OPEN)
                .build();

        //when
        accountRepo.save(expected);
        Optional<Account> actual = accountRepo.findById("test-1");
        log.info("actual = {}", actual);

        //then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}