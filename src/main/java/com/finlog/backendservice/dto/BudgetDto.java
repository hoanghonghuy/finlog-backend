package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetDto {
    private BigDecimal amount;
    private int month;
    private int year;
    private Long categoryId;
}