package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDto {
    private String name;
    private BigDecimal initialBalance;
}