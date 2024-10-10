package com.tradingtrends.stock.presentation.controller;

import com.tradingtrends.stock.application.dto.ApprovalRequest;
import com.tradingtrends.stock.application.service.StockWebSocketKeyService;
import com.tradingtrends.stock.infrastructure.messaging.StockWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class WebsocketController {
    @Value("${stock-api.appkey}")
    private String appKey;

    @Value("${stock-api.appsecret}")
    private String appSecret;

    private final StockWebSocketKeyService stockWebSocketKeyService;
    private final StockWebSocketService stockWebSocketService;

    // 실시간 (웹소켓) 접속키 발급 API
    @GetMapping("/get-approval-key")
    public ResponseEntity<String> getApprovalKey() {
        // Approval Key 발급
        String approvalKey = stockWebSocketKeyService.getApprovalKey(appKey, appSecret);

        return ResponseEntity.ok("Approval Key: " + approvalKey);
    }

    // WebSocket 연결 API
    @PostMapping("/connect-websocket")
    public ResponseEntity<String> connectWebSocket(
            @RequestHeader("approval_key") String approvalKey,
            @RequestHeader("custtype") String custType,
            @RequestHeader("tr_type") String trType,
            @RequestBody ApprovalRequest approvalRequest
    ) {
        stockWebSocketService.connectWebSocket(approvalKey, custType, trType, approvalRequest);
        return ResponseEntity.ok("WebSocket connected with approval key: " + approvalKey);
    }

    // WebSocket 연결 해제 API
    @PostMapping("/disconnect-websocket")
    public ResponseEntity<String> disconnectWebSocket() throws Exception {
        stockWebSocketService.disconnectWebSocket();
        return ResponseEntity.ok("WebSocket disconnected.");
    }
}
