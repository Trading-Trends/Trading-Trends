package com.tradingtrends.auth.presentation.controller;

import com.tradingtrends.auth.application.AuthService;
import com.tradingtrends.auth.presentation.request.LoginRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;  // refresh token 만료 시간

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Void> createAuthenticationToken(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // 토큰 생성
        String accessToken = authService.createAccessToken(loginRequestDto.getUserId(), loginRequestDto.getPassword());
        String refreshToken = authService.createRefreshToken(loginRequestDto.getUserId());

        // refreshToken을 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS를 사용할 때만 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(refreshExpiration)); // 만료시간 설정

        response.addCookie(refreshTokenCookie);

        // accessToken을 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return new ResponseEntity<>(headers, ResponseEntity.ok().build().getStatusCode());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken, @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        authService.logout(accessToken.replace("Bearer ", ""), refreshToken);

        // 쿠키 삭제
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(deleteCookie);

        return ResponseEntity.noContent().build();
    }

    // userId 존재여부 검증 API
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") String userId) {
        Boolean response = authService.verifyUser(userId);
        return ResponseEntity.ok(response);
    }

    // Access Token 재발행 API
    @PostMapping("/refresh-access-token")
    public ResponseEntity<Void> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        // Refresh Token을 이용해 새로운 Access Token 생성
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        // 새로운 Access Token을 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccessToken);

        return new ResponseEntity<>(headers, ResponseEntity.ok().build().getStatusCode());
    }
}
