package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.ExpenseByCategoryDto;
import com.finlog.backendservice.dto.MonthlySummaryDto;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    public MonthlySummaryDto getMonthlySummary(User user, int year, int month) {
        BigDecimal totalIncome = transactionRepository.findTotalAmountByTypeAndMonth(user.getId(), "INCOME", year, month);
        BigDecimal totalExpense = transactionRepository.findTotalAmountByTypeAndMonth(user.getId(), "EXPENSE", year, month);
        return new MonthlySummaryDto(totalIncome, totalExpense);
    }

    public List<ExpenseByCategoryDto> getExpenseByCategory(User user, int year, int month) {
        return transactionRepository.findExpenseByCategoryAndMonth(user.getId(), year, month);
    }
}