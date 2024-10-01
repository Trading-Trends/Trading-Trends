package com.tradingtrends.auth.application;

import com.tradingtrends.auth.infrastructure.client.UserClient;
import com.tradingtrends.auth.infrastructure.client.UserResponse;
import feign.FeignException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;

    public AuthService(UserClient userClient, @Value("${service.jwt.secret-key}") String secretKey, PasswordEncoder passwordEncoder, RedisTemplate<String, Object> redisTemplate) {
        this.userClient = userClient;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    // 로그인 시 Access Token과 Refresh Token을 생성하고 반환 (AccessToken은 응답, RefreshToken은 쿠키로 처리)
    public String createAccessToken(final String userId, final String rawPassword) {
        UserResponse user = retrieveUser(userId);
        validatePassword(rawPassword, user.getPassword());

        // AccessToken만 생성하여 반환 (RefreshToken은 별도로 처리)
        return generateToken(user, accessExpiration);
    }

    public String createRefreshToken(final String userId) {
        UserResponse user = retrieveUser(userId);
        return generateToken(user, refreshExpiration);
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

    // AccessToken 및 RefreshToken 생성 메서드 통합
    private String generateToken(final UserResponse user, Long expirationTime) {
        return Jwts.builder()
                .claim("user_id", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration((new Date(System.currentTimeMillis() + expirationTime)))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // AccessToken 재발행 (RefreshToken은 갱신하지 않음)
    public String refreshAccessToken(final String refreshToken) {
        // 1. RefreshToken이 블랙리스트에 등록되어 있는지 확인
        if (isTokenInBlacklist(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다. 해당 토큰은 이미 로그아웃 처리되었습니다.");
        }

        try {
            // 2. RefreshToken 검증
            Jws<Claims> claims = parseToken(refreshToken);
            String userId = claims.getBody().get("user_id", String.class);
            UserResponse user = retrieveUser(userId);

            // 3. 새로운 AccessToken 발급
            return generateToken(user, accessExpiration);  // 새로운 AccessToken만 생성하여 반환
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh Token이 만료되었습니다.");
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }
    }
    private Jws<Claims> parseToken(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
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

    public void logout(String accessToken, String refreshToken) {
        // Access Token과 Refresh Token을 블랙리스트에 추가
        addTokenToBlacklist(accessToken);
        addTokenToBlacklist(refreshToken);
    }

    private void addTokenToBlacklist(String token) {
        long expiration = getExpirationFromToken(token);
        if (expiration > 0) {  // 만료된 토큰은 블랙리스트에 등록하지 않음
            redisTemplate.opsForValue().set(token, "true", expiration, TimeUnit.MILLISECONDS); // Boolean 대신 String 저장
        }
    }

    private boolean isTokenInBlacklist(String token) {
        // Redis에 저장된 토큰이 있는지 확인
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    private long getExpirationFromToken(String token) {
        Jws<Claims> claims = parseToken(token);
        return claims.getPayload().getExpiration().getTime() - System.currentTimeMillis();
    }
}
