package com.finlog.backendservice.controller;

import com.finlog.backendservice.dto.TransactionDto;
import com.finlog.backendservice.entity.Transaction;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getUserTransactions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionDto transactionDto, @AuthenticationPrincipal User user) {
        Transaction newTransaction = transactionService.addTransaction(transactionDto, user);
        return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto, @AuthenticationPrincipal User user) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDto, user.getId());
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        transactionService.deleteTransaction(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}