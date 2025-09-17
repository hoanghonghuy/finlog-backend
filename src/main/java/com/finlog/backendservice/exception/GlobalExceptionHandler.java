package com.finlog.backendservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Thao tác không thể thực hiện do ràng buộc dữ liệu.";

        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage.contains("violates foreign key constraint")) {
                if (causeMessage.contains("transactions")) {
                    message = "Không thể xóa danh mục hoặc tài khoản đã có giao dịch liên quan.";
                } else if (causeMessage.contains("budgets")) {
                    message = "Không thể xóa danh mục đã được sử dụng trong ngân sách.";
                } else {
                    message = "Không thể xóa do có dữ liệu khác đang liên kết.";
                }
            }
        }

        // Trả về lỗi 409 Conflict - báo hiệu có xung đột với trạng thái hiện tại của dữ liệu
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.CONFLICT);
    }

    // Có thể thêm các ExceptionHandler khác ở đây trong tương lai
}