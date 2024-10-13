package com.tradingtrends.user.application.service;

import com.tradingtrends.user.application.dto.UserDetailsDto;
import com.tradingtrends.user.application.dto.UserResponseDto;
import com.tradingtrends.user.domain.model.User;
import com.tradingtrends.user.domain.repository.UserRepository;
import com.tradingtrends.user.presentation.request.UserRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메서드
    @Transactional
    public void registerUser(UserRequestDto userRequestDto) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        // 유저 객체 생성 및 저장
        User user = User.create(
                userRequestDto.getUsername(),
                encodedPassword,
                userRequestDto.getEmail(),
                User.Role.USER // 기본 권한을 USER로 설정
        );

        // DB에 사용자 정보 저장
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Boolean verifyUser(Long userId) {
        return userRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserDetailsDto getUserDetailsById(Long userId) {
        return userRepository.findByUserId(userId)
                .map(user -> new UserDetailsDto(user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole().toString()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Transactional(readOnly = true)
    public UserDetailsDto getUserDetailsByUsername (String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDetailsDto(user.getUserId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole().toString()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 사용자 정보 수정 메서드
    @Transactional
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 비밀번호 변경 시 암호화
        if (userRequestDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
            user.changePassword(encodedPassword);
        }

        // 이메일 및 기타 정보 업데이트
        if (userRequestDto.getEmail() != null) {
            user.changeEmail(userRequestDto.getEmail());
        }

        if (userRequestDto.getRole() != null) {
            user.changeRole(User.Role.valueOf(userRequestDto.getRole())); // 문자열을 enum으로 변환
        }

        // 업데이트된 사용자 정보 저장
        User updatedUser = userRepository.save(user);

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole().toString()
        );
    }

    // 사용자 삭제 메서드
    @Transactional
    public boolean deleteUser(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 비밀번호가 일치하는지 확인
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            userRepository.delete(user);
            return true;
        } else {
            return false;
        }
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString()
        );
    }
}
