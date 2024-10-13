package com.tradingtrends.auth.presentation.controller;

import com.tradingtrends.auth.application.service.AuthService;
import com.tradingtrends.auth.application.service.TokenService;
import com.tradingtrends.auth.infrastructure.client.UserDetailsDto;
import com.tradingtrends.auth.presentation.request.LoginRequestDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;  // refresh token 만료 시간

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // 사용자 정보 검증
        authService.authenticateUser(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        // 사용자 정보로 토큰 생성
        UserDetailsDto userDetailsDto = authService.retrieveUserByUsername(loginRequestDto.getUsername());
        String accessToken = tokenService.createAccessToken(userDetailsDto);
        String refreshToken = tokenService.createRefreshToken(userDetailsDto);

        // refreshToken을 쿠키에 저장
        addCookieToResponse(response, "refreshToken", refreshToken, refreshExpiration);

        // accessToken을 헤더에 추가
        return addAccessTokenToResponse(accessToken);
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken,
                                       @CookieValue("refreshToken") String refreshToken,
                                       HttpServletResponse response) {
        // Access Token 및 Refresh Token을 로그아웃 처리 (블랙리스트 등록)
        authService.logout(accessToken.replace("Bearer ", ""), refreshToken);

        // RefreshToken 쿠키 삭제
        deleteCookieFromResponse(response, "refreshToken");

        return ResponseEntity.noContent().build();
    }

    // Jwt 검증 API
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestParam("token") String token) {
        return ResponseEntity.ok(new HashMap<>(authService.verifyJwt(token)));
    }

    // Access Token 재발행 API
    @PostMapping("/refresh-access-token")
    public ResponseEntity<Void> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        // Refresh Token을 통해 새로운 Access Token 생성
        String newAccessToken = tokenService.refreshAccessToken(refreshToken);

        // 새로운 AccessToken을 헤더에 추가
        return addAccessTokenToResponse(newAccessToken);
    }

    // Utility 메서드: 쿠키 추가
    private void addCookieToResponse(HttpServletResponse response, String name, String value, Long maxAgeMillis) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 사용 시만 적용
        cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(maxAgeMillis)); // 만료시간 설정
        response.addCookie(cookie);
    }

    // Utility 메서드: 쿠키 삭제
    private void deleteCookieFromResponse(HttpServletResponse response, String name) {
        Cookie deleteCookie = new Cookie(name, null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(deleteCookie);
    }

    // Utility 메서드: AccessToken을 응답 헤더에 추가
    private ResponseEntity<Void> addAccessTokenToResponse(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return new ResponseEntity<>(headers, ResponseEntity.ok().build().getStatusCode());
    }
}
