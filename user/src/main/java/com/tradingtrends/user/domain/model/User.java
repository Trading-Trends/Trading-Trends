package com.tradingtrends.user.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "P_USER", schema = "s_user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class User {

    @Id // 변경된 부분
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 유저 생성 메서드
    public static User create(
            final String username,
            final String password,
            final String email,
            final Role role
    ) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        this.password = newPassword;
    }

    public void changeEmail(String newEmail) {
        if (!newEmail.contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 주소가 아닙니다.");
        }
        this.email = newEmail;
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    // Role enum
    public enum Role {
        USER,
        ADMIN
    }
}
