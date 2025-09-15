package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.TransactionDto;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.Transaction;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.CategoryRepository;
import com.finlog.backendservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction addTransaction(TransactionDto transactionDto, User user) {
        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + transactionDto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không thể tạo giao dịch với danh mục của người khác");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(category);
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa giao dịch này");
        }

        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + transactionDto.getCategoryId()));

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không thể cập nhật giao dịch với danh mục của người khác");
        }

        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa giao dịch này");
        }

        transactionRepository.delete(transaction);
    }
}