package com.finlog.backendservice.service;

import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Thêm import này
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Thêm chuyên gia mã hóa

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) { // Yêu cầu Spring tiêm cả hai
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User userToRegister) {
        if (userRepository.findByUsername(userToRegister.getUsername()).isPresent()) {
            throw new IllegalStateException("Username đã tồn tại");
        }

        if (userRepository.findByEmail(userToRegister.getEmail()).isPresent()) {
            throw new IllegalStateException("Email đã tồn tại");
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(userToRegister.getPassword());
        userToRegister.setPassword(encodedPassword);

        // Lưu người dùng với mật khẩu đã được mã hóa
        return userRepository.save(userToRegister);
    }
}