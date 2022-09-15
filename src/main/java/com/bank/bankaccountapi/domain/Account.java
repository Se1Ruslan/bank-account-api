package com.bank.bankaccountapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ACCOUNT")
public class Account {

    @Id
    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private String accountNumber;

    @Column(name = "CURRENCY", nullable = false)
    private Long currency;

    @Column(name = "BALANCE", nullable = false)
    private Double balance;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return accountNumber != null && Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
