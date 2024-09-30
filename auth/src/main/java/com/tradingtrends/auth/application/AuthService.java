package com.tradingtrends.auth.application;

import com.tradingtrends.auth.application.dto.AuthResponse;
import com.tradingtrends.auth.infrastructure.client.UserClient;
import com.tradingtrends.auth.infrastructure.client.UserResponse;
import feign.FeignException;
import io.jsonwebtoken.*;
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

    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private final SecretKey secretKey;

    public AuthService(UserClient userClient, @Value("${service.jwt.secret-key}") String secretKey, PasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse createAccessToken(final String userId, final String rawPassword) {
        UserResponse user = retrieveUser(userId);
        validatePassword(rawPassword, user.getPassword());
        return generateAccessToken(user);
    }

    private UserResponse retrieveUser(final String userId) throws RuntimeException {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        } catch (FeignException e) {
            throw new RuntimeException("유저 정보 조회 중 문제가 발생했습니다: " + e.getMessage());
        }
    }

    private void validatePassword(final String rawPassword, final String encryptedPassword) throws RuntimeException {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    private AuthResponse generateAccessToken(final UserResponse user) {
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

    public AuthResponse createRefreshToken(final String userId) {
        UserResponse user = retrieveUser(userId);
        return generateRefreshToken(user);
    }

    private AuthResponse generateRefreshToken(final UserResponse user) {
        return AuthResponse.of(Jwts.builder()
                .claim("user_id", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact());
    }

    public AuthResponse refreshAccessToken(final String refreshToken) {
        Jws<Claims> claims = parseToken(refreshToken);
        String userId = claims.getBody().get("user_id", String.class);
        UserResponse user = retrieveUser(userId);
        return generateAccessToken(user);
    }

    private Jws<Claims> parseToken(final String token) {
        try {
            return Jwts.parser()  // parser() 대신 parserBuilder() 사용
                    .setSigningKey(secretKey)  // 시그니처 키 설정
                    .build()  // 빌드 메서드로 파서 인스턴스 생성
                    .parseClaimsJws(token);  // 토큰을 파싱
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }


    public Boolean verifyUser(final String userId) {
        try {
            return userClient.verifyUser(userId);
        } catch (Exception e) {
            throw new RuntimeException("유저 검증 중 문제가 발생했습니다: " + e.getMessage());
        }
    }
}