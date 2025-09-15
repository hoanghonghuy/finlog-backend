package com.finlog.backendservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users") // Đặt tên cho bảng trong CSDL là "users"
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Báo cho CSDL tự động tăng ID
    private Long id;

    @Column(nullable = false, unique = true) // Không được để trống, không được trùng
    private String username;

    @Column(nullable = false, unique = true) // Không được để trống, không được trùng
    private String email;

    @Column(nullable = false) // Không được để trống
    private String password;
}