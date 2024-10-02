package com.tradingtrends.auth.application.service;

import com.tradingtrends.auth.infrastructure.client.UserClient;
import com.tradingtrends.auth.infrastructure.client.UserResponse;
import feign.FeignException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class TokenService {
    private final UserClient userClient;
    private final SecretKey secretKey;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String issuer;
    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;
    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;

    public TokenService(
            UserClient userClient,
            @Value("${service.jwt.secret-key}") String secretKey,
            RedisTemplate<String, Object> redisTemplate) {
        this.userClient = userClient;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.redisTemplate = redisTemplate;
    }

    // Access Token 생성
    public String createAccessToken(UserResponse user) {
        return generateToken(user, accessExpiration);
    }

    // Refresh Token 생성
    public String createRefreshToken(UserResponse user) {
        return generateToken(user, refreshExpiration);
    }

    // Access Token 재발행
    public String refreshAccessToken(String refreshToken) {
        System.out.println(refreshToken);
        if (isTokenInBlacklist(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        try {
            Jws<Claims> claims = parseToken(refreshToken);
            if (claims == null) {
                throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다."); // 추가적인 null 체크
            }
            Integer userId = claims.getPayload().get("user_id", Integer.class);
            UserResponse user = retrieveUser(Long.valueOf(userId)); // retrieveUser 메서드 구현 필요
            return generateToken(user, accessExpiration);
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("Refresh Token이 만료되었습니다.");
        } catch (JwtException e) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
    }

    // 토큰 생성
    private String generateToken(UserResponse user, Long expirationTime) {
        return Jwts.builder()
                .claim("user_id", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // 토큰 파싱
    private Jws<Claims> parseToken(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다: {}", e.getMessage());
            return null; // 만료된 토큰은 null 반환 또는 다른 처리
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            return null; // 유효하지 않은 토큰도 null 반환
        }
    }

    // 토큰의 만료 시간 가져오기
    public long getExpirationFromToken(String token) {
        Jws<Claims> claims = parseToken(token);
        if (claims == null) {
            return 0;
        }
        return claims.getPayload().getExpiration().getTime() - System.currentTimeMillis();
    }

    private boolean isTokenInBlacklist(String token) {
        // Redis에 저장된 토큰이 있는지 확인
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    private UserResponse retrieveUser(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        } catch (FeignException e) {
            throw new RuntimeException("유저 정보 조회 중 문제가 발생했습니다: " + e.status() + " - " + e.getMessage());
        }
    }
}