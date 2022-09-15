package com.bank.bankaccountapi.mapper;

import com.bank.bankaccountapi.domain.Account;
import com.bank.bankaccountapi.dto.AccountDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account map(AccountDto accountDto);

    AccountDto map(Account accountDto);

    List<Account> mapToAccountList(List<AccountDto> accountDtos);

    List<AccountDto> mapToAccountDtoList(List<Account> accountDtos);
}
