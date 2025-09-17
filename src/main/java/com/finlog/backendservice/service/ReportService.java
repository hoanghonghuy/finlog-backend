package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.ExpenseByCategoryDto;
import com.finlog.backendservice.dto.MonthlySummaryDto;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finlog.backendservice.dto.YearlySummaryDto;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map;
import java.util.function.Function;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;

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

    public YearlySummaryDto getYearlySummary(User user, int year) {
        List<YearlySummaryDto.MonthlySummary> summariesFromDb = transactionRepository.findMonthlySummariesForYear(user.getId(), year);

        Map<Integer, YearlySummaryDto.MonthlySummary> summaryMap = summariesFromDb.stream()
                .collect(Collectors.toMap(YearlySummaryDto.MonthlySummary::getMonth, Function.identity()));

        List<YearlySummaryDto.MonthlySummary> fullYearSummaries = IntStream.rangeClosed(1, 12)
                .mapToObj(month -> summaryMap.getOrDefault(month,
                        new YearlySummaryDto.MonthlySummary(month, BigDecimal.ZERO, BigDecimal.ZERO)))
                .sorted(Comparator.comparingInt(YearlySummaryDto.MonthlySummary::getMonth).reversed())
                .collect(Collectors.toList());

        BigDecimal totalIncome = fullYearSummaries.stream()
                .map(YearlySummaryDto.MonthlySummary::getTotalIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = fullYearSummaries.stream()
                .map(YearlySummaryDto.MonthlySummary::getTotalExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new YearlySummaryDto(totalIncome, totalExpense, fullYearSummaries);
    }
}