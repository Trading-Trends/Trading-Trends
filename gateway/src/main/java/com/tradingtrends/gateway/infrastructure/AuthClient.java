package com.tradingtrends.gateway.infrastructure;

import com.tradingtrends.gateway.application.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// 인증 서비스 호출 클라이언트가 응용 계층의 인터페이스인 AuthService 를 상속받아 DIP 를 적용합니다.
@FeignClient(name = "auth")
public interface AuthClient extends AuthService {
    @GetMapping("/api/auth/verify") // Jwt 검증 API
    Map<String, Object> verifyJwt(@RequestParam(value = "token") String token);
}
