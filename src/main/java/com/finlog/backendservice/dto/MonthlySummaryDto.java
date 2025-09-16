package com.finlog.backendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
}