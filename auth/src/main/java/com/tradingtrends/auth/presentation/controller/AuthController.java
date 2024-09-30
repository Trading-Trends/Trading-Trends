package com.tradingtrends.auth.presentation.controller;

import com.tradingtrends.auth.application.AuthService;
import com.tradingtrends.auth.application.dto.AuthResponse;
import com.tradingtrends.auth.presentation.request.LoginRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final String serverPort;

    public AuthController(AuthService authService, @Value("${server.port}") String serverPort) {
        this.authService = authService;
        this.serverPort = serverPort;
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody LoginRequestDto loginRequestDto) {
        AuthResponse response = authService.createAccessToken(loginRequestDto.getUserId(), loginRequestDto.getPassword());
        return createResponse(ResponseEntity.ok(response));
    }

    // userId 존재여부 검증 API
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") String userId) {
        Boolean response = authService.verifyUser(userId);
        return createResponse(ResponseEntity.ok(response));
    }

    // Refresh Token 생성 API
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> createRefreshToken(@RequestParam("user_id") String userId) {
        AuthResponse response = authService.createRefreshToken(userId);
        return createResponse(ResponseEntity.ok(response));
    }

    // Access Token 재발행 API
    @PostMapping("/refresh-access-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        // "Bearer " 접두사를 제외한 토큰 값만 추출
        String token = refreshToken.replace("Bearer ", "");
        AuthResponse response = authService.refreshAccessToken(token);
        return createResponse(ResponseEntity.ok(response));
    }

    // Response Header에 `Server-Port` 추가해주는 함수
    private <T> ResponseEntity<T> createResponse(ResponseEntity<T> response) {
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(response.getHeaders());
        headers.add("Server-Port", serverPort);
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }
}