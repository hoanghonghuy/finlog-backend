package com.finlog.backendservice.controller;

import com.finlog.backendservice.dto.CategoryDto;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getUserCategories(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.getUserCategories(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto, @AuthenticationPrincipal User user) {
        Category createdCategory = categoryService.createCategory(categoryDto, user);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto, @AuthenticationPrincipal User user) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDto, user.getId());
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal User user) {
        categoryService.deleteCategory(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}