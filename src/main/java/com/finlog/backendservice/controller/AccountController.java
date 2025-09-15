package com.finlog.backendservice.controller;

import com.finlog.backendservice.dto.AccountDto;
import com.finlog.backendservice.entity.Account;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getUserAccounts(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto, @AuthenticationPrincipal User user) {
        Account createdAccount = accountService.createAccount(accountDto, user);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto, @AuthenticationPrincipal User user) {
        Account updatedAccount = accountService.updateAccount(id, accountDto, user.getId());
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, @AuthenticationPrincipal User user) {
        accountService.deleteAccount(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}