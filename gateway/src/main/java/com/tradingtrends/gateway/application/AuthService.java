package com.tradingtrends.gateway.application;

import io.jsonwebtoken.Claims;

import java.util.Map;

// 응용 계층 DIP 적용을 위한 인증 서비스 인터페이스
public interface AuthService {
    Map<String, Object> verifyJwt(String token);
}
