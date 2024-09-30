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

    // userId 존재여부 검증 API 입니다.
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("user_id") String userId) {
        Boolean response = authService.verifyUser(userId);
        return createResponse(ResponseEntity.ok(response));
    }

    // Response Header 에 `Server-Port` 룰 추가해주는 Generic 함수입니다.
    private <T> ResponseEntity<T> createResponse(ResponseEntity<T> response) {
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(response.getHeaders()); // 인자로 받은 헤더의 정보를 수정할 수 있도록 불러옵니다.
        headers.add("Server-Port", serverPort); // Response Header 에 Server-Port 키값을 추가합니다.
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode()); // 인자로 받은 값에 수정한 헤더만 적용하여 응답합니다.
    }
}
