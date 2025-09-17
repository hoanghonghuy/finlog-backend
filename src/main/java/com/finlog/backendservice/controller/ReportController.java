package com.finlog.backendservice.controller;

import com.finlog.backendservice.dto.ExpenseByCategoryDto;
import com.finlog.backendservice.dto.MonthlySummaryDto;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.finlog.backendservice.dto.YearlySummaryDto;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @AuthenticationPrincipal User user,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.getMonthlySummary(user, year, month));
    }

    @GetMapping("/expense-by-category")
    public ResponseEntity<List<ExpenseByCategoryDto>> getExpenseByCategory(
            @AuthenticationPrincipal User user,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.getExpenseByCategory(user, year, month));
    }

    @GetMapping("/yearly-summary")
    public ResponseEntity<YearlySummaryDto> getYearlySummary(
            @AuthenticationPrincipal User user,
            @RequestParam int year) {
        return ResponseEntity.ok(reportService.getYearlySummary(user, year));
    }
}