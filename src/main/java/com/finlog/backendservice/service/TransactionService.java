package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.TransactionDto;
import com.finlog.backendservice.dto.TransactionResponseDto; // Import DTO mới
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors; // Import

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // Sửa kiểu trả về
    public List<TransactionResponseDto> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDto) // Dùng mapper
                .collect(Collectors.toList());
    }

    // Sửa kiểu trả về
    public List<TransactionResponseDto> getUserTransactionsByMonth(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::mapToResponseDto) // Dùng mapper
                .collect(Collectors.toList());
    }

    // Sửa kiểu trả về
    @Transactional
    public TransactionResponseDto addTransaction(TransactionDto transactionDto, User user) {
        Category category = null;
        if (transactionDto.getCategoryId() != null) {
            category = categoryRepository.findById(transactionDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + transactionDto.getCategoryId()));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Bạn không thể tạo giao dịch với danh mục của người khác");
            }
        }

        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + transactionDto.getAccountId()));
        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Bạn không thể tạo giao dịch trên tài khoản của người khác");
        }

        updateAccountBalance(account, transactionDto.getAmount(), transactionDto.getType());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(category);
        transaction.setAccount(account);
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponseDto(savedTransaction); // Trả về DTO
    }

    // Sửa kiểu trả về
    @Transactional
    public TransactionResponseDto updateTransaction(Long transactionId, TransactionDto transactionDto, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa giao dịch này");
        }

        revertAccountBalance(transaction.getAccount(), transaction.getAmount(), transaction.getType());

        Account newAccount = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản mới không tồn tại"));
        Category newCategory = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục mới không tồn tại"));
        if (!newAccount.getUser().getId().equals(userId) || !newCategory.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Không thể cập nhật giao dịch với tài khoản hoặc danh mục của người khác");
        }

        updateAccountBalance(newAccount, transactionDto.getAmount(), transactionDto.getType());

        if (!Objects.equals(transaction.getAccount().getId(), newAccount.getId())) {
            accountRepository.save(transaction.getAccount());
        }
        accountRepository.save(newAccount);

        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(transactionDto.getType());
        transaction.setDate(transactionDto.getDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCategory(newCategory);
        transaction.setAccount(newAccount);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToResponseDto(updatedTransaction); // Trả về DTO
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa giao dịch này");
        }

        revertAccountBalance(transaction.getAccount(), transaction.getAmount(), transaction.getType());
        accountRepository.save(transaction.getAccount());

        transactionRepository.delete(transaction);
    }

    // Hàm mapper helper
    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setDescription(transaction.getDescription());

        // Xử lý an toàn nếu category là null
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }

        dto.setAccountId(transaction.getAccount().getId());
        dto.setAccountName(transaction.getAccount().getName());

        return dto;
    }

    private void updateAccountBalance(Account account, BigDecimal amount, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("INCOME".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().add(amount));
        }
    }

    private void revertAccountBalance(Account account, BigDecimal amount, String type) {
        if ("EXPENSE".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().add(amount));
        } else if ("INCOME".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance().subtract(amount));
        }
    }
}