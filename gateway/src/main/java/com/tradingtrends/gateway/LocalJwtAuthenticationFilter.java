package com.tradingtrends.gateway;

import com.tradingtrends.gateway.application.AuthService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(LocalJwtAuthenticationFilter.class);

    // FeignClient 와 Global Filter 의 순환참조 문제가 발생하여 Bean 초기 로딩 시 순환을 막기 위해 @Lazy 어노테이션을 추가함.
    public LocalJwtAuthenticationFilter(@Lazy AuthService authService) {
        this.authService = authService;
    }

    // 필수 과제 - 외부 요청 보호 GlobalFilter
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        // 접근하는 URI 의 Path 값을 받아옵니다.
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("Request Path: {}", path);
        log.info("Request Method: {}", method);

        // /api/auth 경로의 모든 요청은 검증하지 않습니다.
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // /api/member 경로의 POST 요청은 검증하지 않습니다.
        if (path.startsWith("/api/member") && method.equalsIgnoreCase("POST")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        // 토큰이 존재하지 않으면 401 에러를 응답합니다.
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Map<String, Object> claimsMap = validateToken(token);
        // JWT 검증이 실패하면 401 에러를 응답합니다.
        if (claimsMap == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        log.info("User ID: {}", claimsMap.get("user_id"));
        log.info("Username: {}", claimsMap.get("username"));
        log.info("Role: {}", claimsMap.get("role"));
        log.info("Email: {}", claimsMap.get("email"));

        // Claims 정보를 헤더에 추가합니다.
        exchange.getRequest().mutate()
                .header("user_id", claimsMap.get("user_id") != null ? claimsMap.get("user_id").toString() : "")
                .header("username", claimsMap.get("username") != null ? claimsMap.get("username").toString() : "")
                .header("role", claimsMap.get("role") != null ? claimsMap.get("role").toString() : "")
                .header("email", claimsMap.get("email") != null ? claimsMap.get("email").toString() : "")
                .build();

        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        // Request Header 에서 Authorization Key 로 설정된 된 값을 불러옵니다.
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        // 값이 존재하며, Bearer {token} 형태로 시작할 경우
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 값중 앞부분 ('Bearer ') 을 제거하고 응답합니다.
            return authHeader.substring(7);
        }
        return null;
    }

    private Map<String, Object> validateToken(String token) {
        try {
            return authService.verifyJwt(token);
        } catch (Exception e) {
            return null;
        }
    }
}