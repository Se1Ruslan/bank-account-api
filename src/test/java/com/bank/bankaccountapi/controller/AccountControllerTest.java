package com.bank.bankaccountapi.controller;

import com.bank.bankaccountapi.dto.AccountDto;
import com.bank.bankaccountapi.dto.AccountOperationDto;
import com.bank.bankaccountapi.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.bank.bankaccountapi.domain.AccountStatus.OPEN;
import static com.bank.bankaccountapi.dto.OperationSign.CREDIT;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    @SneakyThrows
    void shouldReturnAccountStatus() {
        //given
        when(accountService.getAccountStatusByAccountNumber("test-1"))
                .thenReturn("OPEN");

        //when
        //then
        mockMvc.perform(get("/api/account/status/test-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OPEN")));
    }

    @Test
    @SneakyThrows
    void shouldReturnAccountBalanceInquiry() {
        //given
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("test-1")
                .currency(1L)
                .balance(10.0)
                .status(OPEN)
                .build();
        when(accountService.getAccountBalanceInquiryByAccountNumber("test-1"))
                .thenReturn(accountDto);

        //when

        MvcResult mvcResult = mockMvc.perform(get("/api/account/balance/test-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        AccountDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AccountDto.class);
        log.info("actual = {}", actual);

        //then
        assertEquals(accountDto, actual);
    }

    @Test
    @SneakyThrows
    void shouldApplyAccountOperation() {
        //given
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("test-1")
                .currency(1L)
                .balance(20.0)
                .status(OPEN)
                .build();
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber("test-1")
                .currency(1L)
                .amount(10.0)
                .operationSign(CREDIT)
                .build();
        when(accountService.applyAccountOperation(operation))
                .thenReturn(accountDto);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        AccountDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AccountDto.class);
        log.info("actual = {}", actual);

        //then
        assertEquals(accountDto, actual);
    }

    @Test
    @SneakyThrows
    void shouldValidateApplyAccountOperationRequestBody() {
        //given
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("test-1")
                .currency(1L)
                .balance(20.0)
                .status(OPEN)
                .build();

        when(accountService.applyAccountOperation(any()))
                .thenReturn(accountDto);

        //when
        //then

        //empty body
        mockMvc.perform(post("/api/account/operation"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Required request body is missing")));

        //empty account number
        AccountOperationDto operation = AccountOperationDto.builder()
                .accountNumber(null)
                .currency(1L)
                .amount(10.0)
                .operationSign(CREDIT)
                .build();
        mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("accountNumber must be not empty")));

        //empty currency
        operation = operation.toBuilder()
                .accountNumber("test-1")
                .currency(null)
                .build();
        mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("currency must be not null")));

        //empty amount
        operation = operation.toBuilder()
                .currency(1L)
                .amount(null)
                .build();
        mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("amount must be not null")));

        //negative amount
        operation = operation.toBuilder()
                .amount(-1.0)
                .build();
        mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("amount must be positive")));

        //empty operationSign
        operation = operation.toBuilder()
                .amount(1.0)
                .operationSign(null)
                .build();
        mockMvc.perform(post("/api/account/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("operationSign must be not null")));
    }
}