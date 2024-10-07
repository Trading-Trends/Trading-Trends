package com.tradingtrends.stock.presentation.controller;

import com.tradingtrends.stock.application.service.StockWebSocketKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebsocketController {
    @Value("${stock-api.appkey}")
    private String appKey;

    @Value("${stock-api.appsecret}")
    private String appSecret;

    private final StockWebSocketKeyService stockWebSocketKeyService;

    // 실시간 (웹소켓) 접속키 발급 API
    @GetMapping("/get-approval-key")
    public ResponseEntity<String> getApprovalKey() {
        // Approval Key 발급
        String approvalKey = stockWebSocketKeyService.getApprovalKey(appKey, appSecret);

        return ResponseEntity.ok("Approval Key: " + approvalKey);
    }
}
