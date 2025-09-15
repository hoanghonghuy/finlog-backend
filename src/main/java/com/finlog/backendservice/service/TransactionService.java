package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.TransactionDto;
import com.finlog.backendservice.entity.Account;
import com.finlog.backendservice.entity.Category;
import com.finlog.backendservice.entity.Transaction;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.AccountRepository;
import com.finlog.backendservice.repository.CategoryRepository;
import com.finlog.backendservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional
    public Transaction addTransaction(TransactionDto transactionDto, User user) {
        // Lấy thông tin tài khoản và danh mục
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + transactionDto.getAccountId()));
        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + transactionDto.getCategoryId()));

        // Kiểm tra quyền sở hữu
        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không thể tạo giao dịch trên tài khoản của người khác");
        }
        if (!category.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không thể tạo giao dịch với danh mục của người khác");
        }

        // Cập nhật số dư tài khoản
        updateAccountBalance(account, transactionDto.getAmount(), transactionDto.getType());
        accountRepository.save(account);

        // Tạo và lưu giao dịch mới
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(category);
        transaction.setAccount(account); // Gán tài khoản cho giao dịch
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        // Kiểm tra quyền sở hữu giao dịch
        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa giao dịch này");
        }

        // --- Hoàn tác giao dịch cũ ---
        Account oldAccount = transaction.getAccount();
        revertAccountBalance(oldAccount, transaction.getAmount(), transaction.getType());

        // --- Áp dụng thông tin mới ---
        Account newAccount = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản mới với id: " + transactionDto.getAccountId()));
        Category newCategory = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục mới với id: " + transactionDto.getCategoryId()));

        // Kiểm tra quyền sở hữu tài khoản và danh mục mới
        if (!newAccount.getUser().getId().equals(userId) || !newCategory.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Không thể cập nhật giao dịch với tài khoản hoặc danh mục của người khác");
        }

        // Cập nhật số dư cho tài khoản mới
        updateAccountBalance(newAccount, transactionDto.getAmount(), transactionDto.getType());

        // Nếu tài khoản cũ và mới khác nhau, lưu cả hai
        if (!Objects.equals(oldAccount.getId(), newAccount.getId())) {
            accountRepository.save(oldAccount);
        }
        accountRepository.save(newAccount);


        // Cập nhật thông tin giao dịch
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(newCategory);
        transaction.setAccount(newAccount);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa giao dịch này");
        }

        // Hoàn tác số dư trên tài khoản liên quan
        Account account = transaction.getAccount();
        revertAccountBalance(account, transaction.getAmount(), transaction.getType());
        accountRepository.save(account);

        // Xóa giao dịch
        transactionRepository.delete(transaction);
    }

    // Hàm tiện ích để cập nhật số dư
    private void updateAccountBalance(Account account, BigDecimal amount, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("INCOME".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().add(amount));
        }
    }

    // Hàm tiện ích để hoàn tác số dư
    private void revertAccountBalance(Account account, BigDecimal amount, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().add(amount));
        } else if ("INCOME".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().subtract(amount));
        }
    }
}