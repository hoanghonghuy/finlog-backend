package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.AccountDto;
import com.finlog.backendservice.entity.Account;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.exception.ResourceNotFoundException;
import com.finlog.backendservice.repository.AccountRepository;
import com.finlog.backendservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account createAccount(AccountDto accountDto, User user) {
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setBalance(accountDto.getInitialBalance());
        account.setUser(user);
        return accountRepository.save(account);
    }

    public Account updateAccount(Long accountId, AccountDto accountDto, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền sửa tài khoản này");
        }

        // Chỉ cho phép cập nhật tên, không cho cập nhật số dư trực tiếp
        // Số dư phải được thay đổi thông qua giao dịch để đảm bảo tính toàn vẹn dữ liệu
        account.setName(accountDto.getName());
        return accountRepository.save(account);
    }

    public void deleteAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với id: " + accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa tài khoản này");
        }

        // Kiểm tra an toàn: Nếu tài khoản đã có giao dịch, không cho phép xóa
        if (transactionRepository.existsByAccountId(accountId)) {
            throw new IllegalStateException("Không thể xóa tài khoản đã có giao dịch. Vui lòng xóa các giao dịch liên quan trước.");
        }

        accountRepository.delete(account);
    }
}