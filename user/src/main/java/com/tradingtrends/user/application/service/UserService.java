package com.tradingtrends.user.application.service;

import com.tradingtrends.user.application.dto.UserResponse;
import com.tradingtrends.user.domain.model.User;
import com.tradingtrends.user.domain.repository.UserRepository;
import com.tradingtrends.user.presentation.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메서드
    public User registerUser(UserRequest userRequest) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());

        // 유저 객체 생성 및 저장
        User user = User.create(
                userRequest.getUsername(),
                encodedPassword,
                userRequest.getEmail(),
                User.Role.USER // 기본 권한을 USER로 설정
        );

        return userRepository.save(user);
    }

    public Boolean verifyUser(Long userId) {
        return userRepository.existsByUserId(userId);
    }

    public UserResponse getUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .map(user -> new UserResponse(user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole().toString()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public UserResponse getUserByUsername (String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserResponse(user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole().toString()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
