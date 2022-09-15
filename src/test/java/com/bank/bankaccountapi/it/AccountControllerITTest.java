package com.bank.bankaccountapi.it;

import com.bank.bankaccountapi.BankAccountApiApplication;
import com.bank.bankaccountapi.domain.AccountStatus;
import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;
import com.bank.bankaccountapi.dto.OperationSign;
import com.bank.bankaccountapi.errorhandler.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BankAccountApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerITTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnAccountStatus(){
        //given
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/account/status/test-1"),
                HttpMethod.GET, entity, String.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("OPEN", response.getBody());
    }

    @Test
    public void shouldFailOfReturningAccountStatus(){
        //given
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/status/test-5"),
                HttpMethod.GET, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account does not exists"));
    }

    @Test
    public void shouldReturnAccount(){
        //given
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        AccountDto expected = AccountDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .balance(0.00)
                .status(AccountStatus.OPEN)
                .build();

        //when
        ResponseEntity<AccountDto> response = restTemplate.exchange(
                createURLWithPort("/api/account/balance/test-1"),
                HttpMethod.GET, entity, AccountDto.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void shouldFailOfReturningAccount(){
        //given
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/balance/test-5"),
                HttpMethod.GET, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account does not exists"));
    }

    @Test
    void shouldApplyDebitAccountOperation() {
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-2")
                .currency(840L)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();
        AccountDto expected = AccountDto.builder()
                .accountNumber("test-2")
                .currency(840L)
                .balance(1012.00)
                .status(AccountStatus.OPEN)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<AccountDto> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, AccountDto.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expected, response.getBody());
    }

    @Test
    void shouldApplyCreditAccountOperation() {
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-3")
                .currency(978L)
                .amount(10.0)
                .operationSign(OperationSign.CREDIT)
                .build();
        AccountDto expected = AccountDto.builder()
                .accountNumber("test-3")
                .currency(978L)
                .balance(335.00)
                .status(AccountStatus.OPEN)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<AccountDto> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, AccountDto.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expected, response.getBody());
    }

    @Test
    void shouldNotApplyOperationForNonExistingAccount() {
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("wrong")
                .currency(978L)
                .amount(10.0)
                .operationSign(OperationSign.CREDIT)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account does not exists"));
    }

    @Test
    void shouldNotApplyOperationForNotOpenedAccount(){
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-4")
                .currency(978L)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account is not OPEN"));
    }

    @Test
    void shouldNotApplyOperationWithWrongCurrency() {
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-1")
                .currency(1L)
                .amount(10.0)
                .operationSign(OperationSign.DEBIT)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account and operation currencies are different"));
    }

    @Test
    void shouldNotApplyCreditOperationIfAccountDoesNotHaveEnoughBalance() {
        //given
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-1")
                .currency(978L)
                .amount(10.0)
                .operationSign(OperationSign.CREDIT)
                .build();
        HttpEntity<AccountOperationDto> entity = new HttpEntity<>(operation, new HttpHeaders());

        //when
        ResponseEntity<ApiError> response = restTemplate.exchange(
                createURLWithPort("/api/account/operation"),
                HttpMethod.POST, entity, ApiError.class);
        log.info("response = {}", response);

        //then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Account does not have enough balance to process operation"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
