package com.finlog.backendservice.repository;

import com.finlog.backendservice.dto.ExpenseByCategoryDto;
import com.finlog.backendservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    boolean existsByAccountId(Long accountId);

    // Query để tính tổng thu hoặc tổng chi trong một tháng của một user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    BigDecimal findTotalAmountByTypeAndMonth(@Param("userId") Long userId, @Param("type") String type, @Param("year") int year, @Param("month") int month);

    // Query để tính tổng chi tiêu theo từng danh mục trong một tháng của một user
    @Query("SELECT new com.finlog.backendservice.dto.ExpenseByCategoryDto(c.name, COALESCE(SUM(t.amount), 0)) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND YEAR(t.date) = :year AND MONTH(t.date) = :month " +
            "GROUP BY c.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<ExpenseByCategoryDto> findExpenseByCategoryAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId AND t.type = 'EXPENSE' AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    BigDecimal findTotalExpenseByCategoryIdAndMonth(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("year") int year,
            @Param("month") int month
    );
}