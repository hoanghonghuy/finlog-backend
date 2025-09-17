package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionResponseDto {
    private Long id;
    private BigDecimal amount;
    private String type;
    private LocalDate date;
    private String description;

    private Long categoryId;
    private String categoryName;
    private Long accountId;
    private String accountName;
}