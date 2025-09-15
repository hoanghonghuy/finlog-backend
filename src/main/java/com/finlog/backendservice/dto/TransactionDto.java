package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionDto {
    private BigDecimal amount;
    private String type; // "INCOME" hoặc "EXPENSE"
    private LocalDate date;
    private String description;
    private Long categoryId;
}