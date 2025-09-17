package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetWithSpendingDto {
    private Long id;
    private BigDecimal amount; // Số tiền đã đặt ngân sách
    private BigDecimal actualSpending; // Số tiền đã chi tiêu thực tế
    private int month;
    private int year;
    private CategoryDto category;
}