package com.finlog.backendservice.controller;

import com.finlog.backendservice.dto.BudgetDto;
import com.finlog.backendservice.dto.BudgetWithSpendingDto; // Sửa import này
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetWithSpendingDto>> getUserBudgetsByMonth( // Sửa kiểu trả về
                                                                              @RequestParam int year,
                                                                              @RequestParam int month,
                                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(budgetService.getUserBudgetsByMonth(user.getId(), year, month));
    }

    @PostMapping
    public ResponseEntity<BudgetWithSpendingDto> createBudget(@RequestBody BudgetDto budgetDto, @AuthenticationPrincipal User user) { // Sửa kiểu trả về
        BudgetWithSpendingDto createdBudget = budgetService.createBudget(budgetDto, user);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetWithSpendingDto> updateBudget(@PathVariable Long id, @RequestBody BudgetDto budgetDto, @AuthenticationPrincipal User user) { // Sửa kiểu trả về
        BudgetWithSpendingDto updatedBudget = budgetService.updateBudget(id, budgetDto, user.getId());
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id, @AuthenticationPrincipal User user) {
        budgetService.deleteBudget(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}