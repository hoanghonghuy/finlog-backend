package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.BudgetDto;
import com.finlog.backendservice.dto.BudgetWithSpendingDto;
import com.finlog.backendservice.dto.CategoryDto;
import com.finlog.backendservice.entity.Budget;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.BudgetRepository;
import com.finlog.backendservice.repository.CategoryRepository;
import com.finlog.backendservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public List<BudgetWithSpendingDto> getUserBudgetsByMonth(Long userId, int year, int month) {
        return budgetRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .stream()
                .map(budget -> mapToDtoWithSpending(budget, userId, year, month))
                .collect(Collectors.toList());
    }

    public BudgetWithSpendingDto createBudget(BudgetDto budgetDto, User user) {
        Category category = categoryRepository.findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + budgetDto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không có quyền tạo ngân sách cho danh mục của người khác");
        }

        budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                user.getId(), budgetDto.getCategoryId(), budgetDto.getYear(), budgetDto.getMonth()
        ).ifPresent(b -> {
            throw new IllegalStateException("Ngân sách cho danh mục này đã tồn tại trong tháng/năm đã chọn");
        });

        Budget budget = new Budget();
        budget.setAmount(budgetDto.getAmount());
        budget.setMonth(budgetDto.getMonth());
        budget.setYear(budgetDto.getYear());
        budget.setCategory(category);
        budget.setUser(user);

        Budget savedBudget = budgetRepository.save(budget);
        return mapToDtoWithSpending(savedBudget, user.getId(), savedBudget.getYear(), savedBudget.getMonth());
    }

    public BudgetWithSpendingDto updateBudget(Long budgetId, BudgetDto budgetDto, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách với id: " + budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa ngân sách này");
        }

        budget.setAmount(budgetDto.getAmount());

        Budget updatedBudget = budgetRepository.save(budget);
        return mapToDtoWithSpending(updatedBudget, userId, updatedBudget.getYear(), updatedBudget.getMonth());
    }

    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách với id: " + budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa ngân sách này");
        }

        budgetRepository.delete(budget);
    }

    private BudgetWithSpendingDto mapToDtoWithSpending(Budget budget, Long userId, int year, int month) {
        BigDecimal actualSpending = transactionRepository.findTotalExpenseByCategoryIdAndMonth(
                userId,
                budget.getCategory().getId(),
                year,
                month
        );

        BudgetWithSpendingDto dto = new BudgetWithSpendingDto();
        dto.setId(budget.getId());
        dto.setAmount(budget.getAmount());
        dto.setActualSpending(actualSpending);
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(budget.getCategory().getId());
        categoryDto.setName(budget.getCategory().getName());
        dto.setCategory(categoryDto);

        return dto;
    }
}