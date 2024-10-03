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

    // Role enum
    public enum Role {
        USER,
        ADMIN
    }
}
