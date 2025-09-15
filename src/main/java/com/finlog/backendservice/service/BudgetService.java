package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.BudgetDto;
import com.finlog.backendservice.entity.Budget;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.BudgetRepository;
import com.finlog.backendservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public List<Budget> getUserBudgetsByMonth(Long userId, int year, int month) {
        return budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);
    }

    public Budget createBudget(BudgetDto budgetDto, User user) {
        Category category = categoryRepository.findById(budgetDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + budgetDto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không có quyền tạo ngân sách cho danh mục của người khác");
        }

        // Kiểm tra xem đã tồn tại ngân sách cho danh mục này trong tháng/năm đó chưa
        budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                user.getId(),
                budgetDto.getCategoryId(),
                budgetDto.getYear(),
                budgetDto.getMonth()
        ).ifPresent(b -> {
            throw new IllegalStateException("Ngân sách cho danh mục này đã tồn tại trong tháng/năm đã chọn");
        });

        Budget budget = new Budget();
        budget.setAmount(budgetDto.getAmount());
        budget.setMonth(budgetDto.getMonth());
        budget.setYear(budgetDto.getYear());
        budget.setCategory(category);
        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long budgetId, BudgetDto budgetDto, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách với id: " + budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa ngân sách này");
        }

        // Logic: Khi cập nhật, chỉ cho phép cập nhật số tiền
        // Việc thay đổi tháng/năm/danh mục nên được thực hiện bằng cách xóa và tạo mới
        budget.setAmount(budgetDto.getAmount());

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách với id: " + budgetId));

        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa ngân sách này");
        }

        budgetRepository.delete(budget);
    }
}