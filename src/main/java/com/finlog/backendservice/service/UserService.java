package com.finlog.backendservice.service;

import com.finlog.backendservice.dto.LoginRequest;
import com.finlog.backendservice.dto.LoginResponse;
import com.finlog.backendservice.entity.User;
import com.finlog.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User registerUser(User userToRegister) {
        if (userRepository.findByUsername(userToRegister.getUsername()).isPresent()) {
            throw new IllegalStateException("Username đã tồn tại");
        }
        if (userRepository.findByEmail(userToRegister.getEmail()).isPresent()) {
            throw new IllegalStateException("Email đã tồn tại");
        }
        String encodedPassword = passwordEncoder.encode(userToRegister.getPassword());
        userToRegister.setPassword(encodedPassword);
        return userRepository.save(userToRegister);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username không hợp lệ"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(user.getId(), token);
    }
}