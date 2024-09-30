package com.tradingtrends.auth.application;

import com.tradingtrends.auth.application.dto.AuthResponse;
import com.tradingtrends.auth.infrastructure.client.UserClient;
import com.tradingtrends.auth.infrastructure.client.UserResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;

    public AuthService(UserClient userClient, @Value("${service.jwt.secret-key}") String secretKey, PasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse createAccessToken(final String userId, final String rawPassword) {
        // UserClient를 사용해 회원 정보를 조회하고 유효한 회원인지 확인
        UserResponse user = userClient.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 입력된 비밀번호와 저장된 암호화된 비밀번호를 비교
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호가 일치하면 JWT 토큰 생성
        return AuthResponse.of(Jwts.builder()
                .claim("user_id", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact());
    }


    // 회원 존재 여부 검증 로직
    public Boolean verifyUser(final String userId) {
        return userClient.verifyUser(userId);
    }
}
