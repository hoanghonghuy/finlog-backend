package com.finlog.backendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class YearlySummaryDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<MonthlySummary> monthlySummaries;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MonthlySummary {
        private int month;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
    }
}