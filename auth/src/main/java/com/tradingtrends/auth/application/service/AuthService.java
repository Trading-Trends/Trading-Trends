package com.tradingtrends.auth.application.service;

import com.tradingtrends.auth.infrastructure.client.UserClient;
import com.tradingtrends.auth.infrastructure.client.UserDetailsDto;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthService(UserClient userClient, PasswordEncoder passwordEncoder, TokenService tokenService, RedisTemplate<String, Object> redisTemplate) {
        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.redisTemplate = redisTemplate;
    }

    // 로그인 시 사용자 정보 검증
    public void authenticateUser(String username, String rawPassword) {
        UserDetailsDto user = retrieveUserByUsername(username);
        validatePassword(rawPassword, user.getPassword());
    }

    public UserDetailsDto retrieveUserByUsername(final String username) throws RuntimeException {
        try {
            return userClient.getUserDetailsByUsername(username);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        } catch (FeignException e) {
            throw new RuntimeException("유저 정보 조회 중 문제가 발생했습니다: " + e.status() + " - " + e.getMessage());
        }
    }

    private void validatePassword(final String rawPassword, final String encryptedPassword) throws RuntimeException {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    public Claims verifyJwt(final String token) {
        try {
            // 토큰이 블랙리스트에 있는지 확인
            if (isTokenInBlacklist(token)) {
                throw new RuntimeException("해당 토큰은 블랙리스트에 등록되어 있습니다.");
            }

            // JWT 토큰 파싱
            Jws<Claims> jws = tokenService.parseToken(token);
            Claims claims = jws.getPayload(); // getPayload() 대신 getBody() 사용
            Long userId = claims.get("user_id", Long.class);

            // 유저 존재 여부 확인
            if (userClient.verifyUser(userId)) {
                return claims; // 유효한 경우 Claims 반환
            } else {
                throw new RuntimeException("유저 검증에 실패하였습니다.");
            }
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("유저 검증 중 문제가 발생했습니다: " + e.getMessage());
        }
    }


    // 로그아웃 처리 및 블랙리스트에 토큰 추가
    public void logout(String accessToken, String refreshToken) {
        addTokenToBlacklist(accessToken);
        addTokenToBlacklist(refreshToken);
    }

    private void addTokenToBlacklist(String token) {
        try {
            long expiration = tokenService.getExpirationFromToken(token);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(token, "true", expiration, TimeUnit.MILLISECONDS);
            }
        } catch (ExpiredJwtException e) {
            // 토큰이 이미 만료된 경우 아무 작업도 하지 않음
            System.out.println("토큰이 이미 만료되었습니다: " + token);
        }
    }

    private boolean isTokenInBlacklist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
