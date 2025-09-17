package com.finlog.backendservice.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BudgetResponseDto {
    private Long id;
    private BigDecimal amount;
    private int month;
    private int year;
    private CategoryDto category;
}