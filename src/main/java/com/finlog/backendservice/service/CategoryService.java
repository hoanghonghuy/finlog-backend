package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.CategoryDto;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getUserCategories(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Category createCategory(CategoryDto categoryDto, User user) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long categoryId, CategoryDto categoryDto, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + categoryId));

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa danh mục này");
        }

        category.setName(categoryDto.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + categoryId));

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa danh mục này");
        }

        categoryRepository.delete(category);
    }
}